### Build runtime image
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=client-adapter/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 3000