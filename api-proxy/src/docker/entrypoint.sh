#!/bin/sh
set -e

/etc/init.d/filebeat start -e -v

java -server \
  -Djava.security.egd=file:/dev/./urandom \
  -DLOG_FILE=api-proxy.log \
  -DLOG_PATH=/var/log/ \
  -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
  -jar app.jar \
  --server.port=8080
