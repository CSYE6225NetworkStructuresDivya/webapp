#!/bin/bash

# Move the jar file to the /opt/csye6225 directory
sudo mkdir -p /opt/csye6225
sudo mv /tmp/assignment-0.0.1-SNAPSHOT.jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
sudo mv /tmp/application-start.service /etc/systemd/system/
echo "########################### Files moved ########################### "

# Check if the JAR file exists
if [ -f "/opt/csye6225/assignment-0.0.1-SNAPSHOT.jar" ]; then
    echo "########################### JAR file exists at: /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar ########################### "
else
    echo "########################### Error: JAR file not found at: /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar ########################### "
    exit 1
fi

# Create user csye6225
sudo groupadd csye6225
sudo useradd -s /bin/false -g csye6225 -d /opt/csye6225 -m csye6225
echo "########################### User csye6225 created ########################### "

# Change the ownership of the jar file
sudo chown csye6225:csye6225 /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
echo "########################### Ownership changed ########################### "
id csye6225

# Install Java Development Kit
sudo yum -y install java-17-openjdk-devel
echo "########################### Java Development Kit installed ########################### "
echo $JAVA_HOME
java -version

# Install MySQL
sudo yum install -y mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld
echo "########################### MySQL installed and started ########################### "

# Create a database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cloud_assignment;"

# Check if the command executed successfully
if [ $? -eq 0 ]; then
  echo "########################### Database created ########################### "
else
  echo "########################### Failed to create database ########################### "
  exit 1
fi

# Enable and start the application
#sudo java -jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
sudo systemctl daemon-reload
sudo systemctl enable application-start

echo "#################### setup complete! ####################"