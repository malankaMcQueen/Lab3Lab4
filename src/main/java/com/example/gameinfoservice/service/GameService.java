package com.example.gameinfoservice.service;

import com.example.gameinfoservice.model.Genre;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GameService {

  //Получение информации об игре с API
  public String getGameById(String id) {
    String url = "https://api.rawg.io/api/games/" + id + "?key=b71a35bd7c7b43928005918c4b5b3cc6";
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.getForObject(url, String.class);
    JsonElement jsonElement = JsonParser.parseString(response);
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    JsonObject extractedFields = new JsonObject();

    extractedFields.add("name", jsonObject.get("name"));
    extractedFields.add("description", jsonObject.get("description"));
    extractedFields.add("genres", jsonObject.get("genres"));

    Gson gson = new Gson();

    return gson.toJson(extractedFields);
  }

  //Получение какого-либо поля игры с API
  public String getGameFieldValue(String id, String field) {
    String url = "https://api.rawg.io/api/games/" + id + "?key=b71a35bd7c7b43928005918c4b5b3cc6";
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.getForObject(url, String.class);
    JsonElement jsonElement = JsonParser.parseString(response);
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    return jsonObject.get(field).getAsString();
  }
}