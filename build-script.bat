call api-gateway-build-script.bat
cd ../
call passenger-service-build-script.bat
cd ../
call driver-service-build-script.bat
cd ../
call ride-service-build-script.bat
cd ../
call payment-service-build-script.bat
cd ../
call docker-compose up -d