#!/bin/sh

# Start zipkin which now has eureka support
java -Dloader.path='eureka.jar,eureka.jar!/lib' -Dspring.application.name=zipkin-server -cp zipkin.jar org.springframework.boot.loader.PropertiesLauncher
