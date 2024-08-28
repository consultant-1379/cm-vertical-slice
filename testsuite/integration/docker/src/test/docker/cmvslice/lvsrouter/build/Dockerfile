FROM armdocker.rnd.ericsson.se/proj_oss/os/centos:7

RUN yum -y install net-tools iproute iptables ipvsadm keepalived openssh-server 2>/dev/null

RUN echo -e "12shroot\n12shroot" | passwd 2>/dev/null

RUN ssh-keygen -A

COPY etc/ /etc/

COPY scripts/* /usr/sbin/

CMD /usr/sbin/start-container.sh
