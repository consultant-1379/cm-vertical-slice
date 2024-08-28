#!/bin/bash
BASEDIR=$(dirname "$(readlink -f "$0")")
SCRIPTNAME=$(basename "$0")
COMPOSEDIR="${BASEDIR}/testsuite/integration/docker/src/test/docker/cmvslice"
COMPOSEPREFIX=$(basename ${COMPOSEDIR})
export COMPOSE_HTTP_TIMEOUT=360

_help() {
	echo
	echo "Usage: ${SCRIPTNAME} [help|version|clean|pull|build|start|restart|test|logs|stop|all]"
	echo
	echo "   help     Display this message"
	echo "   version  Display version of docker components"
	echo "   clean    Clean docker containers, images and volumes"
	echo "   pull     Pull docker images and build containers"
	echo "   build    Build containers only"
	echo "   start    Start containers"
	echo "   restart  Restart containers"
	echo "   test     Run tests"
	echo "   logs     Collect container logs"
	echo "   stop     Stop containers"
	echo "   all      Pull images, start containers, run tests, stop"
	echo
	echo "  Set the variable ENM_ISO_VERSION to use a specific set of docker images."
	echo
	echo "  e.g.:"
	echo "     ENM_ISO_VERSION=1.76.2 ./${SCRIPTNAME} all"
	echo
	echo "  NOTE: This VS can be used to test SNAPSHOT builds of the following EARs:"
	echo
	echo "   - network-element-connector-receiver-ear"
	echo "   - subscription-creation-handler-code-ear"
	echo "   - subscription-validation-handler-code-ear"
	echo "   - inbound-dps-handler-code-ear"
	echo "   - sync-node-moci-handler-ear"
	echo "   - notification-receiver-handler-code-ear"
	echo "   - cm-router-policy-ear"
	echo
	echo "  To test a SNAPSHOT EAR: build it locally, then run this script by setting the"
	echo "  PROJECTS variable to the path of the GIT repo just built."
	echo
	echo "  e.g.:"
	echo "     PROJECTS=/home/user/git/inbound-dps-handler-code ./${SCRIPTNAME} all"
	echo
}

_version() {
	echo
	docker-compose version
	docker --version
	docker version
	echo
}

_clean() {
	cd ${COMPOSEDIR}

	_stop

	dockercontainers=$(docker ps -qaf "name=${COMPOSEPREFIX}")
	if [ -n "${dockercontainers}" ]; then
		echo "Stopping containers:"
		docker stop -t 30 ${dockercontainers}
		docker rm ${dockercontainers}
	fi

	dockervolumes=$(docker volume ls -qf dangling=true)
	if [ -n "${dockervolumes}" ]; then
		echo "Removing volumes:"
		docker volume rm ${dockervolumes}
	fi

	dockernetworks=$(docker network ls -qf "name=${COMPOSEPREFIX}")
	if [ -n "${dockernetworks}" ]; then
		echo "Removing networks:"
		docker network rm ${dockernetworks}
	fi

	_cleanimages
	_cleanlogs
}

_cleanlogs() {
	cd ${BASEDIR}
	rm -f logs/*.log

	cd ${COMPOSEDIR}
	if [ -d output ]; then
		docker run --rm -v $(readlink -f output):/output \
			centos bash -c "rm -rf /output/*"
	fi
}

_cleanimages() {
	dockerimages=$(docker images -qf dangling=true)
	if [ -n "${dockerimages}" ]; then
		echo "Removing dangling images:"
		docker rmi -f ${dockerimages}
	fi
}

_pull() {
	cd ${COMPOSEDIR}
	echo "Pulling docker images and building services"
	docker-compose pull
	docker-compose build --pull

	_cleanimages
}

_build() {
	cd ${COMPOSEDIR}
	echo "Building services"
	docker-compose build

	_cleanimages
}

_start() {
	cd ${COMPOSEDIR}
	echo "Starting services"
	MEDLOGLEVEL=TRACE docker-compose up -d

	for service in $(docker-compose config --services | sort); do
		if [ -n "$(docker ps -q --no-trunc | grep $(docker-compose ps -q ${service}))" ]; then
			port=$(docker-compose port ${service} 9990)
			if [ -n "${port}" ]; then
				waitForJBossService ${service} ${port}
			fi
		fi
	done
}

waitForJBossService() {
	service=${1}
	port=${2}

	echo "Checking the status of JBoss container ${service} (${port}) ..."
	delay=10
	attempts=100
	counter=0
	while true; do
		if [ -z "$(docker ps -q --no-trunc | grep $(docker-compose ps -q ${service}))" ]; then
			echo "ERROR: JBoss service ${service} is not running, Exiting...."
			exit 1
		fi
		check=$(curl --digest --user 'root:shroot' --silent "${port}/management")
		if [[ ${check} ]]; then
			echo "JBoss service ${service} is ready, moving on..."
			break
		fi
		counter=$((counter+1))
		if [ ${counter} -gt ${attempts} ]; then
			echo "ERROR: Issue starting the JBoss service ${service} in the container, Exiting...."
			exit 1
		fi
		echo "Waiting for JBoss service ${service} to start. Have tried ${counter} times .. Sleeping for ${delay} seconds. Will try up to ${attempts} attempts ..."
		sleep ${delay}
	done
}

_test() {
	_cleanlogs
	cd ${BASEDIR}

	echo "Running tests"
	mvn -f testsuite/integration/docker/pom.xml -U -B clean install -Pexecute_docker_tests

	_logs
}

_logs() {
	cd ${COMPOSEDIR}
	echo "Copying logs to ${BASEDIR}/logs"
	mkdir -p ${BASEDIR}/logs

	for service in $(docker-compose config --services); do
		id=$(docker-compose ps -q ${service})
		if [ -n "${id}" -a -n "$(docker ps -q --no-trunc | grep "${id}")" ]; then
			container=$(docker ps -a --format '{{.Names}}' --filter id=${id})
			port=$(docker-compose port ${service} 9990)
			if [ -n "${port}" ]; then
				echo "Copying ${container}-server.log"
				docker cp ${container}:/ericsson/3pp/jboss/standalone/log/server.log ${BASEDIR}/logs/${container}-server.log
			fi
			echo "Copying ${container}.log"
			docker logs ${container} &> ${BASEDIR}/logs/${container}.log
		fi
	done
}

_stop() {
	cd ${COMPOSEDIR}
	echo "Stopping services"
	docker-compose down -t 5
}

_restart() {
	_stop
	_start
}

_all() {
	_pull
	_start
	_test
	_stop
}

if (( $# != 1 )); then
	_help
	exit -1
fi
case "$1" in
help|version|clean|pull|build|start|restart|test|logs|stop|all)
	_$1
	;;
*)
	echo
	echo "Command not recognized: '${SCRIPTNAME} $1', run '${SCRIPTNAME} help' for usage."
	echo
	exit -2
	;;
esac
