package com.company.smartgarage.helpers;

import com.company.smartgarage.models.Maintenance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.List;

@Component
public class RateHelper {
    public static final String BGN = "BGN";
    public static final String API_URL_BGN = "https://api.polygon.io/v2/aggs/ticker/C:%sBGN/prev?adjusted=true&apiKey=%s";
    public static final String API_URL = "https://api.polygon.io/v2/aggs/ticker/C:%s%s/prev?adjusted=true&apiKey=%s";
    private static String apiKey;

    @Value("${api.key}")
    public void setProperty(final String propertyName) {
        apiKey = propertyName;
    }

    public static double getRate(String currency, String previousCurrency)  {
        String url = "";
        if (previousCurrency.equals(BGN) && currency.equals(BGN)) {
            return 1;
        }
        if (previousCurrency.equals(BGN)) {
            //BGN -> EUR, BGN -> USD, BGN -> GBP
            url = String.format(
                    API_URL_BGN,
                    currency, apiKey);
        } else {
            url = String.format(
                    API_URL,
                    previousCurrency, currency, apiKey);
        }
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);

        double rate = 1;
        if (result != null) {
            rate = Double.parseDouble(result.split(",")[6].split(":")[1]);
        }

        return rate;
    }

    public static double calculatePrice(String fromCurrency, double price, double rate) {

        DecimalFormat df = new DecimalFormat("#.##");

        if (fromCurrency.equals(BGN)) {
            price /= rate;
        } else {
            price *= rate;
        }
        return Double.parseDouble(df.format(price));
    }

    public static List<Maintenance> calculatePrice(String fromCurrency, List<Maintenance> services, double rate) {

        DecimalFormat df = new DecimalFormat("#.##");

        double price = 0;
        for (Maintenance service : services) {
            price = service.getPrice();

            if (fromCurrency.equals(BGN)) {
                price /= rate;
            } else {
                price *= rate;
            }
            price = Double.parseDouble(df.format(price));
            service.setPrice(price);
        }
        return services;
    }
}
