package com.example.httphandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.logging.Logger;
import org.json.JSONObject;

@SpringBootApplication
@RestController
@RequestMapping("/api/data")
public class HttpHandlerApplication {

    private static final Logger logger = Logger.getLogger(HttpHandlerApplication.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(HttpHandlerApplication.class, args);
    }

    @PostMapping
    public String receiveData(@RequestBody String data) {
        logger.info("Получен HTTP-запрос с данными: " + data);
        saveToDatabase(data);
        return "Данные успешно сохранены";
    }

    private void saveToDatabase(String data) {
        try {

            JSONObject json = new JSONObject(data);
            String name = json.optString("name", ""); // name
            int age = json.optInt("age", 0); // age

            // запрос
            String sql = "INSERT INTO messages (name, age, received_at) VALUES (?, ?, NOW())";
            logger.info(String.format("Подготовка запроса: INSERT INTO messages (name, age) VALUES ('%s', %d)", name, age));
            jdbcTemplate.update(sql, name, age);

            logger.info("Данные сохранены в БД: " + data);
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }
}