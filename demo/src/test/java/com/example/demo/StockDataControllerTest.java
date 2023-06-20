package com.example.demo;

import com.example.demo.Controller.StockDataController;
import com.example.demo.Model.StockData;
import com.example.demo.Service.StockDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StockDataControllerTest {
    @Mock
    private StockDataService stockDataService;

    private StockDataController stockDataController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        stockDataController = new StockDataController(stockDataService);
    }

    @Test
    public void testRefreshCharts_ReturnsStockDataList() throws IOException {
        // Arrange
        String stockTicker = "GOOGL";
        String startDate = "2023-06-01";
        String endDate = "2023-06-10";
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<StockData> expectedStockDataList = Collections.singletonList(new StockData());

        when(stockDataService.getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate)))
                .thenReturn(expectedStockDataList);

        // Act
        ResponseEntity<List<StockData>> response = stockDataController.refreshCharts(stockTicker, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStockDataList, response.getBody());
        verify(stockDataService, times(1)).getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate));
    }



    @Test
    public void testRefreshCharts_ReturnsInternalServerError_WhenIOExceptionOccurs() throws IOException {
        // Arrange
        String stockTicker = "GOOGL";
        String startDate = "2023-06-01";
        String endDate = "2023-06-10";
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        when(stockDataService.getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate)))
                .thenThrow(new IOException());

        // Act
        ResponseEntity<List<StockData>> response = stockDataController.refreshCharts(stockTicker, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(stockDataService, times(1)).getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate));
    }
}
