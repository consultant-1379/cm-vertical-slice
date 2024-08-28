#!/bin/bash
BASEDIR=$(dirname "$(readlink -f "$0")")
SCRIPTNAME=$(basename "$0")
COMPOSEDIR="${BASEDIR}"
COMPOSEPREFIX=$(basename ${COMPOSEDIR})
export COMPOSE_HTTP_TIMEOUT=360

docker_compose() {
  docker-compose "$@"
}

_help() {
  echo
  echo "Usage: ${SCRIPTNAME} [help|version|up|down|gui]"
  echo
  echo "   help     Display this message"
  echo "   version  Display the version of docker components"
  echo "   up       Start the netsim service with CM Vertical Slice nodes"
  echo "   down     Stop the netsim service"
  echo "   gui      Start the netsim service and run the GUI"
  echo
}

_version() {
  echo
  docker_compose version
  docker --version
  docker version
  echo
}

upNetsim() {
  echo "Starting Netsim service..."
  docker_compose up --no-color --no-recreate -d
}

downNetsim() {
  echo "Stopping Netsim service..."
  docker_compose down --volumes -t 5
}

waitForNetsim() {
  echo "Checking the status of Netsim service..."
  delay=10
  attempts=20
  counter=0
  while true; do
    if [ -z "$(docker ps -q --no-trunc | grep $(docker_compose ps -q netsim))" ]; then
      echo "ERROR: Netsim service is not running, Exiting...."
      downNetsim
      exit 1
    fi
    # wait for entrypoint.sh to get to 'tail -f /dev/null'
    check=$(docker_compose exec -T netsim su netsim -c "pgrep --count tail")
    if [[ ${check} -ge 1 ]]; then
      echo "Netsim is ready, moving on..."
      break
    fi
    counter=$((counter+1))
    if [ ${counter} -gt ${attempts} ]; then
      echo "ERROR: Issue starting Netsim, Exiting...."
      downNetsim
      exit 1
    fi
    echo "Waiting for Netsim to start. Have tried ${counter} times .. Sleeping for ${delay} seconds. Will try up to ${attempts} attempts ..."
    sleep ${delay}
  done
}

setupCmVsSims() {
  echo "Setting up CM Vertical Slice sims..."
  if ! docker_compose exec -T netsim /netsim/sims/setupCmVsSims.sh; then
    echo "ERROR: Issue setting up CM Vertical Slice sims, Exiting...."
    downNetsim
    exit 1
  fi
}

attachToNetsim() {
  echo "Running Netsim GUI..."
  docker_compose exec --user 'netsim' netsim bash -c 'test "$(tail -1 /netsim/inst/netsimgui/jsrc/runNetsim)" = "wait" || echo "wait" >> /netsim/inst/netsimgui/jsrc/runNetsim'
  docker_compose exec --user 'netsim' netsim bash -c 'bash --rcfile <(echo ". ~/.bashrc; trap \"pkill -f NetsimUI\" EXIT; ./inst/netsim_gui")'
}

_up() {
  upNetsim
  waitForNetsim
  setupCmVsSims
}

_down() {
  downNetsim
}

_gui() {
  upNetsim
  waitForNetsim
  setupCmVsSims
  attachToNetsim
}

cd "${COMPOSEDIR}"

if (( $# == 0 )); then
  _help
  exit -1
fi
for cmd in "$@"; do
  case "${cmd}" in
  help|version|up|down|gui)
    _${cmd}
    ;;
  *)
    echo
    echo "Command not recognized: '${SCRIPTNAME} ${cmd}', run '${SCRIPTNAME} help' for usage."
    echo
    exit -2
    ;;
  esac
done
