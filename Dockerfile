FROM openjdk:8-jdk-alpine

ARG JAR_FILE=core-connector/target/*.jar

COPY ${JAR_FILE} app.jar

### IMPORTANT: keep the pattern DFSP_UPPERCASE adding new vars
###
ENV MLCONN_OUTBOUND_ENDPOINT="http://simulator:3004"
ENV DFSP_NAME="DFSP CO. LTD."
ENV DFSP_HOST="https://localhost/api"
ENV DFSP_USERNAME="username"
ENV DFSP_PASSWORD="password"
ENV DFSP_AUTH_CLIENT_ID="clientId"
ENV DFSP_AUTH_CLIENT_SECRET="clientSecret"
ENV DFSP_AUTH_GRANT_TYPE="grantType"
ENV DFSP_AUTH_SCOPE="scope"
ENV DFSP_AUTH_ENCRYPTED_PASS="false"
ENV DFSP_AUTH_TENANT_ID="tenantId"
ENV DFSP_AUTH_CHANNEL_ID="channelId"
ENV DFSP_AUTH_API_KEY="apiKey"
ENV DFSP_API_VERSION="v1"
ENV DFSP_AGENT_MOB_NUM="1234"
ENV DFSP_AGENT_MPIN="1234"
ENV DFSP_AGENT_TPIN="1234"
ENV DFSP_PRODUCT_ID="1234"

### IMPORTANT: add new entry adding new vars
###
ENTRYPOINT ["java", "-Dml-conn.outbound.host=${MLCONN_OUTBOUND_ENDPOINT}", "-Ddfsp.name=${DFSP_NAME}", "-Ddfsp.host=${DFSP_HOST}", "-Ddfsp.username=${DFSP_USERNAME}", "-Ddfsp.password=${DFSP_PASSWORD}", "-Ddfsp.scope=${DFSP_AUTH_SCOPE}", "-Ddfsp.client-id=${DFSP_AUTH_CLIENT_ID}", "-Ddfsp.client-secret=${DFSP_AUTH_CLIENT_SECRET}", "-Ddfsp.grant-type=${DFSP_AUTH_GRANT_TYPE}", "-Ddfsp.is-password-encrypted=${DFSP_AUTH_ENCRYPTED_PASS}", "-Ddfsp.tenant-id=${DFSP_AUTH_TENANT_ID}", "-Ddfsp.channel-id=${DFSP_AUTH_CHANNEL_ID}", "-Ddfsp.api-key=${DFSP_AUTH_API_KEY}", "-Ddfsp.api-version=${DFSP_API_VERSION}","-Ddfsp.agent.mobile-number=${DFSP_AGENT_MOB_NUM}", "-Ddfsp.agent.mpin=${DFSP_AGENT_MPIN}", "-Ddfsp.agent.tpin=${DFSP_AGENT_TPIN}", "-Ddfsp.product-id=${DFSP_PRODUCT_ID}", "-jar", "/app.jar"]

EXPOSE 3003