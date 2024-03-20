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

# Set up logging
sudo mkdir -p /var/log/csye6225
sudo touch /var/log/csye6225/application.log
sudo chown csye6225:csye6225 /var/log/csye6225/application.log

# Check if the log file exists
if [ -f "/var/log/csye6225/application.log" ]; then
    echo "########################### Application log file exixts ########################### "
else
    echo "########################### Application log file not found ########################### "
    exit 1
fi

# Install Ops Agent
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install

echo "########################### Changing config.yaml ########################### "
sudo cp -f /tmp/config.yaml /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent

# Check the status of the Ops Agent
echo "########################### Google-cloud-ops-agent status start ########################### "
sudo systemctl status google-cloud-ops-agent

# Check config.yaml file for the log file path
echo "########################### Cat config.yaml ########################### "
cat /etc/google-cloud-ops-agent/config.yaml

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