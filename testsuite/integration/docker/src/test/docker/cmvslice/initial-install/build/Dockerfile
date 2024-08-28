FROM armdocker.rnd.ericsson.se/proj_oss/os/centos:7

ARG MCAST_ADDR
ARG MCAST_MESSAGING_ADDR
ARG ENM_SHARED_DIR
ARG LVSROUTER_IP_INTERNAL
ARG CM_VIP
ARG MSCM

ADD root/ssh.tar.gz /root/

RUN mkdir -p ${ENM_SHARED_DIR}/{vmlockfile,byteman}

COPY shared/ ${ENM_SHARED_DIR}/

RUN ${ENM_SHARED_DIR}/scripts/update-properties.sh

CMD rm -rf ${ENM_SHARED_DIR}/byteman/*
