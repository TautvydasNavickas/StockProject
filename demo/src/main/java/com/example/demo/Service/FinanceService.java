package com.example.demo.Service;

import com.example.demo.Model.StockData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceService {
    StockData stockData;
    private static final String API_KEY = "2ce7137d60f366d7d94e6afe86883ddc"; // Replace with your API key
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public List<StockData> getStockData(String ticker, LocalDate startDate, LocalDate endDate) throws IOException {
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
        if (historicalNode.isArray()) {
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
