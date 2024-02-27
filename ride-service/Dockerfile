FROM openjdk:17

WORKDIR /application

COPY target/ride-service-0.0.1-SNAPSHOT.jar ride-service.jar

ENTRYPOINT ["java", "-jar", "ride-service.jar"]