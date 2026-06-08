package com.example.soul.controller;

import com.example.soul.dto.TranslateRequest;
import com.example.soul.service.TranslateService;
import org.springframework.web.bind.annotation.*;

//翻译
@RestController
@RequestMapping("/api/translate")
@CrossOrigin
public class TranslateController {

    private final TranslateService translateService;

    public TranslateController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @PostMapping
    public String translate(@RequestBody TranslateRequest request) {

        return translateService.translate(
                request.getText(),
                request.getTargetLang()
        );
    }
}