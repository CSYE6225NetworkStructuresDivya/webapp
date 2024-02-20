#!/bin/bash

echo 'Build finished!'

jar_file="/opt/csye6225/assignment-0.0.1-SNAPSHOT.jar"

# Check if the JAR file exists
if [ -f "$jar_file" ]; then
    echo "JAR file exists at: $jar_file"
else
    echo "Error: JAR file not found at: $jar_file"
    # Handle the error or exit the script
    exit 1
fi

# Run the jar file
#sudo chown csye6225:csye6225 /opt/csye6225/gatewayapplication-0.0.1-SNAPSHOT.jar
sudo java -jar /opt/csye6225/assignment-0.0.1-SNAPSHOT.jar