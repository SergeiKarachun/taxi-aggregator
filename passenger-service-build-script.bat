cd passenger-service

call mvn clean package -DskipTests
call docker build -t passenger-service:0.0.1 .