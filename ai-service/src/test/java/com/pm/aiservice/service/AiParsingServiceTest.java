package com.pm.aiservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiParsingServiceTest {

    @Autowired
    private AiParsingService aiParsingService;

    @Test
    void testParse() {
        String input = "Book appointment tomorrow 5pm with Dr Patel for dental checkup";

        var response = aiParsingService.parse(input);

        System.out.println("Response: "+response);
    }
}