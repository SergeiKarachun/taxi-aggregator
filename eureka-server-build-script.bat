cd eureka-server

call mvn clean package -DskipTests
call docker build -t eureka-server:0.0.1 .