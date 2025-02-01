package com.example.mqttlistener;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@SpringBootApplication
public class MqttListenerApplication implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(MqttListenerApplication.class.getName());
    private static final String MQTT_BROKER = "tcp://localhost:1883";
    private static final String MQTT_TOPIC = "test/topic";
    private static final String HTTP_ENDPOINT = "http://localhost:8081/api/data";

    public static void main(String[] args) {
        SpringApplication.run(MqttListenerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MqttClient client = new MqttClient(MQTT_BROKER, MqttClient.generateClientId());
        client.connect();
        logger.info("Подключение к MQTT-брокеру установлено");

        client.subscribe(MQTT_TOPIC, (topic, message) -> {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            logger.info("Получено сообщение из MQTT: " + payload);
            sendToHttp(payload);
        });

    }

    private void sendToHttp(String data) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(HTTP_ENDPOINT, data, String.class);
            logger.info("Сообщение успешно отправлено через HTTP: " + data);
        } catch (Exception e) {
            logger.severe("Ошибка при отправке HTTP-запроса: " + e.getMessage());
        }
    }
}

