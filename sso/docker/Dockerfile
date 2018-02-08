FROM home1oss/java:oracle-8u131-jdk-alpine

MAINTAINER data-platform

ADD *.jar /root/
ADD entrypoint.sh /root/

RUN touch /root/*.jar
RUN chmod +x /root/*.sh

EXPOSE 9090
VOLUME ["/root/data/"]

WORKDIR /root

ENTRYPOINT ["/root/entrypoint.sh"]
CMD []

