package com.dmitrromashov.controller;

import com.dmitrromashov.service.StrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StrideController {

    private StrideService strideService;


    @Autowired
    public StrideController(StrideService service){
        this.strideService = service;

    }

    @GetMapping("/descriptor")
    public String returnDescriptor(){
        String ngrokUrl = "https://40d5a156.ngrok.io";
        String strideBotKey = "stride-bot-key-drom";

        String json =
                "{" +
                    "\"baseUrl\": \"" + ngrokUrl + "\", " +
                    "\"key\": \"" + strideBotKey + "\"," +
                    "\"lifecycle\": " +
                        "{ " +
                            "\"installed\": \"/installed\", " +
                            "\"uninstalled\": \"/uninstalled\" " +
                        "}," +
                    "\"modules\": " +
                        "{" +
                            "\"chat:bot\": " +
                                    "[ " +
                                        "{ " +
                                            "\"key\": \"my-bot\", \"mention\": { \"url\": \"/bot-mention\" }," +
                                            "\"directMessage\": { \"url\": \"/bot-dm\"}" +
                                        "}" +
                                    "]" +
                        "}" +
                "}";
        return json;
    }

    @PostMapping("/installed")
    public void installAction(){
        System.out.println("App installed");

    }


    @PostMapping("/uninstalled")
    public void uninstallAction(){
        System.out.println("App uninstalled");

    }

    @PostMapping("/bot-mention")
    public void botMention(@RequestBody String message){
        strideService.sendAnswerToBotMention(message);
    }
}
