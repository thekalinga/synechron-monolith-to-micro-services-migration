FROM openjdk:8-jre
MAINTAINER Dev Team <dev@example.com>

ENV FILEBEAT_VERSION=6.2.3 \
  FILEBEAT_CFG=/etc/filebeat/filebeat.yml \
  SERVER_PORT=18080

# Down
RUN apt-get update -qq && \
    apt-get install -qqy wget && \
    wget -O filebeat-${FILEBEAT_VERSION}-amd64.deb https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-${FILEBEAT_VERSION}-amd64.deb && \
    wget -O filebeat-${FILEBEAT_VERSION}-amd64.deb.sha512 https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-${FILEBEAT_VERSION}-amd64.deb.sha512 && \
    sha512sum -c filebeat-${FILEBEAT_VERSION}-amd64.deb.sha512 && \
    dpkg -i filebeat-${FILEBEAT_VERSION}-amd64.deb && \
    rm filebeat-${FILEBEAT_VERSION}-amd64.deb && \
    rm filebeat-${FILEBEAT_VERSION}-amd64.deb.sha512 && \
    rm -rf /var/lib/apt/lists/*

## Add Filebeat configuration
ADD src/docker/filebeat.yml /etc/filebeat/filebeat.yml

RUN chmod go-w /etc/filebeat/filebeat.yml

# There seems to be an issue with certificates generated via `genCerts.sh` script
# https://discuss.elastic.co/t/tls-connection-failed-because-of-certificate-signed-by-unknown-authority/57064/15
## Add CA certificate
#RUN mkdir -p /etc/pki/tls/certs
#ADD src/docker/logstash.crt /etc/pki/tls/certs/logstash.crt

COPY build/libs/order-service-1.0.0-SNAPSHOT.jar /app.jar
COPY src/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["/entrypoint.sh"]
