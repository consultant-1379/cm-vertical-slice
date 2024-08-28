#!/bin/bash
export COMPOSE_HTTP_TIMEOUT=360

composePath=$1
composePrefix=$(basename ${composePath})
jenkinsFetchCommitsScript="/proj/ciexadm200/tools/utils/fast_commit/fetch_commits.groovy"
ciEnv=$(grep lciadm100 /etc/passwd)

main(){
    echo 'Starting Pre Build Tasks...'

    if [[ ${ciEnv} ]]; then
        echo 'Fetch and install required commits...'
        groovy ${jenkinsFetchCommitsScript}
        checkError $? ${jenkinsFetchCommitsScript}
    else
        echo "Not in CI environment. Will not fetch commits"
    fi
    performCleanUp
    dockerLaunch
}

checkError(){
    status=$1
    script=$2
    if [[ ${status} -ne 0 ]]; then
        echo "Exit Error ${status} thrown from script: "${script}
        echo "Exiting..."
        exit 1
    fi
}

performCleanUp(){
    echo "Cleaning up before execution"

    echo "Executing: docker-compose down -v --remove-orphans"
    docker-compose down -v --remove-orphans

    dockercontainers=$(docker ps -q)
    if [ -n "${dockercontainers}" ]; then
        echo "Executing: docker stop ${dockercontainers}"
        docker stop ${dockercontainers}
    fi
    echo "Executing: docker container prune -f"
    docker container prune -f

    echo "Executing: docker volume prune -f"
    docker volume prune -f

    echo "Executing: docker network prune -f"
    docker network prune -f

    if [[ ${ciEnv} ]]; then
        dockerimages=$(docker images -f dangling=true | grep ${composePrefix}_ | awk '{print $3}')
    else
        dockerimages=$(docker images | awk '{print $3}' | grep -vi Image)
    fi
    for image in ${dockerimages}; do
        echo "Executing: docker rmi ${image}"
        docker rmi ${image}
    done
}

dockerLaunch(){
    echo "Creating and starting docker containers "
    docker-compose pull || exit 1
    docker-compose build --pull || exit 1
    docker-compose up --force-recreate -d || exit 1

    # Find me the list of services that have jboss in their prefixes
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

cd ${composePath}
main
