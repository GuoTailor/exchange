package com.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BaseTest {
    @Test
    public void testRecord() throws JsonProcessingException {
        var user = new User("nmka", 12);
        var json = new ObjectMapper();
        String s = json.writeValueAsString(user);
        System.out.println(s);
        User user1 = json.readValue(s, User.class);
        System.out.println(user1);
    }

    @Test
    public void testTime() throws JsonProcessingException {
        Duration duration = Duration.ofNanos(LocalTime.MAX.toNanoOfDay());
        System.out.println(duration.toNanos());
        System.out.println(duration.toSecondsPart());
        LocalTime localTime = LocalTime.ofSecondOfDay(72);
        System.out.println(localTime.toNanoOfDay());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println(objectMapper.writeValueAsString(duration));
    }
}
