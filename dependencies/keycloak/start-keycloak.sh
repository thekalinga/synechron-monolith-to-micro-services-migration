#!/bin/sh

# Mapping default 8080 port of keycloak to 9999 on the host
# Also, make sure you se a different domain name say `idp` by mapping this to 127.0.0.1 in localhost to avoid cookie sharing issue between auth server & actual application

docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password \
    -e KEYCLOAK_IMPORT=/tmp/demo-realm.json \
    -v $(pwd)/demo-realm.json:/tmp/demo-realm.json \
    -p 9999:8080 \
    jboss/keycloak
