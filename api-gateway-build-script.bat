cd api-gateway

call mvn clean package -DskipTests
call docker build -t api-gateway:0.0.1 .