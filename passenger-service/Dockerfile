FROM openjdk:17

WORKDIR /application

COPY target/passenger-service-0.0.1-SNAPSHOT.jar passenger-service.jar

ENTRYPOINT ["java", "-jar", "passenger-service.jar"]