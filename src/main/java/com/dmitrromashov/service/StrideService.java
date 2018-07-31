package com.dmitrromashov.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class StrideService {

    public void sendAnswerToBotMention(String message) {
        JsonObject messageObj = new JsonParser().parse(message).getAsJsonObject();
        String cloudId = messageObj.get("cloudId").getAsString();
        String conversationId = messageObj.get("conversation").getAsJsonObject().get("id").getAsString();
        String messageText = messageObj.get("message").getAsJsonObject().get("text").getAsString();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
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
            httpPost.setEntity(new StringEntity(sendJsonString));
            String authStr = getAccessToken();

            httpPost.setHeader("authorization", authStr);
            httpPost.setHeader("cache-control", "no-cache");
            httpPost.setHeader("content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("Response status " + response.getStatusLine().getStatusCode());
            response.close();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private String getAccessToken() {
        String accessToken = "";
        String tokenType = "";
        String clientId = "nQJLGhBjdkrtcds75DkQHLBNxjsWAH4J";
        String clientSecret = "HFec4i_0zvtKHz4YUT29vq1Bj1vW6PRGjG82Oq9thvqbI3c6Jksn2RpzYAidgbG8";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://api.atlassian.com/oauth/token";

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("cache-control", "no-cache");
            httpPost.setHeader("content-type", "application/json");

            JsonObject authJsonObj = new JsonObject();
            authJsonObj.addProperty("grant_type", "client_credentials");
            authJsonObj.addProperty("client_id", clientId);
            authJsonObj.addProperty("client_secret", clientSecret);
            String sendJsonString = authJsonObj.toString();
            httpPost.setEntity(new StringEntity(sendJsonString));
            CloseableHttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity httpEntity = response.getEntity();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    stringBuilder.append(line);
                }
                String responseStr = stringBuilder.toString();
                JsonObject responseJsonObj = new JsonParser().parse(responseStr).getAsJsonObject();
                accessToken = responseJsonObj.get("access_token").getAsString();
                tokenType = responseJsonObj.get("token_type").getAsString();
            }
            response.close();


        } catch (Exception e) {
            e.printStackTrace();

        }

        return tokenType + " " + accessToken;

    }

}
