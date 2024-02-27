SELECT 'CREATE DATABASE passenger'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'passenger')\gexec
SELECT 'CREATE DATABASE driver'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'driver')\gexec
SELECT 'CREATE DATABASE payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment')\gexec
