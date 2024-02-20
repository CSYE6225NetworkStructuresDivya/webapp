#!/bin/bash

# Move the jar file to the /opt/csye6225 directory
sudo mkdir -p /opt/csye6225
sudo mv /tmp/assignment-0.0.1-SNAPSHOT.jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
echo "Jar file moved"

# Run the SQL script
sudo yum install -y mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld

echo "MySQL installed and started"

# Install Java Development Kit
sudo yum -y install java-17-openjdk-devel
echo "Java Development Kit installed"

java -version

# Create a database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cloud_assignment;"

# Check if the command executed successfully
if [ $? -eq 0 ]; then
  echo "Database created"
else
  echo "Failed to create database"
fi