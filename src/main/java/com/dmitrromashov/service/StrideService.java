package com.dmitrromashov.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class StrideService {
    public String startAuth(HttpSession session) {


        return null;
    }

    public void finishAuthAndSaveUserDetails(HttpSession session, Map<String, String[]> parameterMap) {

    }

    public void sendAnswerToBotMention(String message){
        JsonObject messageObj = new JsonParser().parse(message).getAsJsonObject();
        String cloudId = messageObj.get("cloudId").getAsString();
        String conversationId = messageObj.get("conversation").getAsJsonObject().get("id").getAsString();
        String messageText = messageObj.get("message").getAsJsonObject().get("text").getAsString();
        System.out.println("cloudId " + cloudId);
        System.out.println("conversationId " + conversationId);
        System.out.println("messageText " + messageText);



        try (CloseableHttpClient client = HttpClients.createDefault()){
            String url = "https://api.atlassian.com/site/" + cloudId + "/conversation/user/" + conversationId + "/message";

            HttpPost httpPost = new HttpPost(url);

            JsonObject sendJsonObj = new JsonObject();
            JsonObject bodyObj = new JsonObject();
            bodyObj.addProperty("version", 1);
            bodyObj.addProperty("type", "doc");
            JsonArray contentArr = new JsonArray();
            JsonObject contentArrObj = new JsonObject();
            contentArrObj.addProperty("type", "paragraph");
            JsonArray contentArrObjContentArr = new JsonArray();
            JsonObject contentArrObjContentObj = new JsonObject();
            contentArrObjContentObj.addProperty("type", "text");
            contentArrObjContentObj.addProperty("text", messageText);
            contentArrObjContentArr.add(contentArrObjContentObj);

            contentArrObj.add("content", contentArrObjContentArr);
            contentArr.add(contentArrObj);

            bodyObj.add("content", contentArr);
            sendJsonObj.add("body", bodyObj);
            String sendJsonString = sendJsonObj.toString();
            System.out.println("Json obj " + sendJsonString);
            httpPost.setEntity(new StringEntity(sendJsonString));
            testPost();
            String authStr = getAccessToken();
            httpPost.setHeader("authorization", authStr);
//            httpPost.setHeader("cache-control", "no-cache");
            httpPost.setHeader("cache-control", "no-cache, no-store, must-revalidate");
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader("pragma", "no-cache");
            httpPost.setHeader("expires", "0");
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("Response status " + response.getStatusLine().getStatusCode());
            System.out.println("Reason Phrase " + response.getStatusLine().getReasonPhrase());
            response.close();


        } catch (Exception e){
            e.printStackTrace();

        }

    }

    private String getAccessToken(){
        String accessToken = "";
        String tokenType = "";
        try (CloseableHttpClient client = HttpClients.createDefault()){
            String url = "https://api.atlassian.com/oauth/token";

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader("cache-control", "no-cache, no-store, must-revalidate");
            httpPost.setHeader("pragma", "no-cache");
            httpPost.setHeader("expires", "0");

            JsonObject authJsonObj = new JsonObject();
            authJsonObj.addProperty("grant_type", "client_credentials");
            authJsonObj.addProperty("client_id", "GPvwrGWgxPG08OuFs5CUtPAXn0Lr1bcU");
            authJsonObj.addProperty("client_secret", "krcqXOQzfFmniFZ3p-1mZNvLz0rigvalqrtbpr1TRShoEb7LYGN0fQdnMxSyf4pk");
            String sendJsonString = authJsonObj.toString();
            httpPost.setEntity(new StringEntity(sendJsonString));
            CloseableHttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response status " + statusCode);
            if (statusCode == 200){
                HttpEntity httpEntity = response.getEntity();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null){
                    System.out.println(line);
                    stringBuilder.append(line);
                }
                String responseStr = stringBuilder.toString();
                JsonObject responseJsonObj = new JsonParser().parse(responseStr).getAsJsonObject();
                accessToken = responseJsonObj.get("access_token").getAsString();
                tokenType = responseJsonObj.get("token_type").getAsString();
                System.out.println("Auth str " + tokenType + " " + accessToken );
                int t = 3;
            }
            response.close();


        } catch (Exception e){
            e.printStackTrace();

        }
        String authStr = tokenType + " " + accessToken;
        System.out.println(authStr);
//        testGet();
//        testPost();

        return authStr;

    }

    private void testPost(){
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "https://google.com";

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("content-type", "application/json");
        httpPost.setHeader("cache-control", "no-cache, no-store, must-revalidate");
        httpPost.setHeader("pragma", "no-cache");
        httpPost.setHeader("expires", "0");

        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Response status " + statusCode);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testGet(){
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "https://google.com";

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("content-type", "application/json");
        httpGet.setHeader("cache-control", "no-cache, no-store, must-revalidate");
        httpGet.setHeader("pragma", "no-cache");
        httpGet.setHeader("expires", "0");

        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Response status " + statusCode);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
