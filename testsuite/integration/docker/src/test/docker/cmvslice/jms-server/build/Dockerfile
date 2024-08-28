FROM armdocker.rnd.ericsson.se/proj_enm/jms-server:1.9.1

RUN sed -i 's/4096/1024/g' /ericsson/3pp/jboss/jboss-as.conf

RUN mv -f /usr/sbin/start-container.sh /usr/sbin/start-container.sh.old

COPY jboss/config/* /ericsson/3pp/jboss/standalone/configuration/

COPY scripts/* /usr/sbin/
