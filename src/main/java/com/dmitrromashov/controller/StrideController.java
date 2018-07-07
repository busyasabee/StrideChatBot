package com.dmitrromashov.controller;

import com.dmitrromashov.service.StrideService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;

@RestController
//@RequestMapping("/stridechat/*")
public class StrideController {
    private StrideService strideService;
    @GetMapping("/start-auth")
    @ResponseStatus(TEMPORARY_REDIRECT)
    public void strideAuthStart(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String authorizePageUrl = strideService.startAuth(request.getSession(true));
        response.sendRedirect(authorizePageUrl);
    }

    @GetMapping("/finish-auth")
    @ResponseStatus(TEMPORARY_REDIRECT)
    public void strideAuthFinish(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        strideService.finishAuthAndSaveUserDetails(request.getSession(true), request.getParameterMap());
        response.setStatus(OK.value());
    }

    @GetMapping("/descriptor")
    public String returnDescriptor(){
        String json = "{ \"baseUrl\": \"https://48a4c367.ngrok.io\", " +
                "\"key\": \"stride-bot\"," +
                "\"lifecycle\": {\"installed\": \"/installed\", \"uninstalled\": \"/uninstalled\"}," +
                "\"modules\": {}}";
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
}
