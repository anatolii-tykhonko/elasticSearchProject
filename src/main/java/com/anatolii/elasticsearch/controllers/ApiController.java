package com.anatolii.elasticsearch.controllers;

import com.anatolii.elasticsearch.service.EsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EsService esService;

    public ApiController(EsService esService) {
        this.esService = esService;
    }

    @GetMapping("start")
    public String start(){
        return "I'm here!";
    }

    @GetMapping ("/articles")
    public String addArticle(@RequestParam("title") String title, @RequestParam("text") String text) throws IOException {
        String id = UUID.randomUUID().toString();
        esService.updateArticle(id, title, text);
        return id;
    }
}
