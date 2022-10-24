package com.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

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
}
