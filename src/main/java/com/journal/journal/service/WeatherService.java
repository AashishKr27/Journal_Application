package com.journal.journal.service;

import com.journal.journal.api.response.WeatherResponse;
import com.journal.journal.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        if(city==null || city.isEmpty()) return null;

        WeatherResponse value = redisService.getValue(city, WeatherResponse.class);
        if(value != null) return value;

        String URL = appCache.APP_CACHE
                .get("weather_api")
                .replace("<city>", city)
                .replace("<apiKey>", apiKey);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(URL, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body = response.getBody();
        if(body != null) {
            redisService.setKey(city, body, 300L);
            return body;
        }
        return null;
    }
}
