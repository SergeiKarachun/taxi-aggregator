cd ride-service

call mvn clean package -DskipTests
call docker build -t ride-service:0.0.1 .