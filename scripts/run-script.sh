#!/bin/bash

# Move the jar file to the /opt/csye6225 directory
sudo mkdir -p /opt/csye6225
sudo mv /tmp/assignment-0.0.1-SNAPSHOT.jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
sudo mv /tmp/application-start.service /etc/systemd/system/
sudo chmod +x /etc/systemd/system/application-start.service
sudo chmod +x /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
echo "########################### Files moved ########################### "

# Check if the JAR file exists
if [ -f "/opt/csye6225/assignment-0.0.1-SNAPSHOT.jar" ]; then
    echo "########################### JAR file exists at: /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar ########################### "
else
    echo "########################### Error: JAR file not found at: /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar ########################### "
    exit 1
fi

# Check if the JAR file exists
if [ -f "/etc/systemd/system/application-start.service" ]; then
    echo "########################### Service file exixts ########################### "
else
    echo "########################### Service file not found ########################### "
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
which java

# Enable and start the application
#sudo java -jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar
sudo systemctl daemon-reload
sudo systemctl enable application-start

echo "#################### setup complete! ####################"