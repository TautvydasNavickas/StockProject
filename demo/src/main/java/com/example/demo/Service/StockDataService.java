package com.example.demo.Service;

import com.example.demo.Model.StockData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StockDataService {
    @Value("${api.key}")
    private String API_KEY;
    public static final ObjectMapper objectMapper = new ObjectMapper();


    private final Cache<String, List<StockData>> stockDataCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();


    public List<StockData> getStockData(String ticker, LocalDate startDate, LocalDate endDate) throws IOException {
        String cacheKey = ticker + startDate.toString() + endDate.toString();
        List<StockData> cachedData = stockDataCache.getIfPresent(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }


        List<StockData> stockDataList = fetchStockData(ticker, startDate, endDate);

        calculateMovingAverages(stockDataList);

        stockDataCache.put(cacheKey, stockDataList);

        return stockDataList;
    }


    private void calculateMovingAverages(List<StockData> stockDataList) {
        // Calculate moving averages for each data point
        for (int i = 0; i < stockDataList.size(); i++) {
            if (i >= 4) {
                double sum5 = 0;
                for (int j = i - 4; j <= i; j++) {
                    sum5 += stockDataList.get(j).getClose();
                }
                double movingAverage5 = sum5 / 5;
                stockDataList.get(i).setMovingAverage5(movingAverage5);
            }

            if (i >= 19) {
                double sum20 = 0;
                for (int j = i - 19; j <= i; j++) {
                    sum20 += stockDataList.get(j).getClose();
                }
                double movingAverage20 = sum20 / 20;
                stockDataList.get(i).setMovingAverage20(movingAverage20);
            }
        }
    }

    public List<StockData> fetchStockData(String ticker, LocalDate startDate, LocalDate endDate) throws IOException {
        String encodedTicker = URLEncoder.encode(ticker, StandardCharsets.UTF_8);
        String encodedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String encodedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = "https://financialmodelingprep.com/api/v3/historical-price-full/" +
                encodedTicker +
                "?from=" + encodedStartDate +
                "&to=" + encodedEndDate +
                "&apikey=" + API_KEY;

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity);

        List<StockData> stockDataList = new ArrayList<>();

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode historicalNode = rootNode.get("historical");
        if (historicalNode != null && historicalNode.isArray()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (JsonNode node : historicalNode) {
                String date = node.get("date").asText();
                LocalDate currentDate = LocalDate.parse(date, dateFormatter);

                if (!currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)) {
                    StockData stockData = new StockData();
                    stockData.setDate(LocalDate.parse(date));
                    stockData.setOpen(node.get("open").asDouble());
                    stockData.setHigh(node.get("high").asDouble());
                    stockData.setLow(node.get("low").asDouble());
                    stockData.setClose(node.get("close").asDouble());
                    stockData.setVolume(node.get("volume").asLong());
                    stockDataList.add(stockData);
                }
            }
        }

        return stockDataList;
    }
}