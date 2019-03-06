#!/bin/sh

# Make sure to set AUTH_CODE variable in the shell before calling this
# run like this
# AUTH_CODE=<long auth code> ./exchange-authcode.sh

http -v --form \
  --auth spring-security:very-very-secret \
  --auth-type basic \
  POST \
  http://idp:9999/auth/realms/demo/protocol/openid-connect/token \
  grant_type=authorization_code \
  code=$AUTH_CODE \
  redirect_uri=http://localhost:8080/login/oauth2/code/keycloak \
  "Accept:application/json"

export TOKEN=$(http --form --auth inventory-service-client:8ae855cf-2751-4104-a0d4-5f04a08d5827 --auth-type basic POST http://idp:9999/auth/realms/demo/protocol/openid-connect/token grant_type=client_credentials | jq '.access_token' | sed 's/"//g')
