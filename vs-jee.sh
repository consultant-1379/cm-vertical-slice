#!/bin/bash
mvn -V clean install -Psetup_neo4j,execute_jee_tests -Dnetsim_docker "$@"
