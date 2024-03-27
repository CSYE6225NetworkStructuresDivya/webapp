package com.cloud.assignment.controller;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PubSubPublisher {
    private static final String PROJECT_ID = "cloud-csye-6225";

    @Value("${gcloud_pubsub_topic_id}")
    private String TOPIC_ID;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public void publishMessage(String email) {
        try {
            Publisher publisher = Publisher.newBuilder(ProjectTopicName.of(PROJECT_ID, TOPIC_ID)).build();

            //Create a message
            Map<String, String> message = new HashMap<>();
            message.put("email", email);

            Gson gson = new Gson();
            String json = gson.toJson(message);

            ByteString data = ByteString.copyFromUtf8(json);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            publisher.publish(pubsubMessage);
            publisher.shutdown();

            logger.info("Message published: " + json);

        } catch (Exception e) {
            logger.error("Error publishing message: " + e.getMessage());
        }
    }
}
