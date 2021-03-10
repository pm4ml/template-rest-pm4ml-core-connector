### Build runtime image
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=client-adapter/target/*.jar
COPY ${JAR_FILE} app.jar
ENV MLCONN_OUTBOUND_ENDPOINT=http://simulator:3003
ENTRYPOINT ["java","-Doutbound.endpoint=${MLCONN_OUTBOUND_ENDPOINT}","-jar","/app.jar"]
EXPOSE 3002