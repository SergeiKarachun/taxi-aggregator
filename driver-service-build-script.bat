cd driver-service

call mvn clean package -DskipTests
call docker build -t driver-service:0.0.1 .