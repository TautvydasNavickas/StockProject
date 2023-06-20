package com.example.demo;

import com.example.demo.Controller.FinanceController;
import com.example.demo.Model.StockData;
import com.example.demo.Service.FinanceService;
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

public class FinanceControllerTest {
    @Mock
    private FinanceService financeService;

    private FinanceController financeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        financeController = new FinanceController(financeService);
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

        when(financeService.getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate)))
                .thenReturn(expectedStockDataList);

        // Act
        ResponseEntity<List<StockData>> response = financeController.refreshCharts(stockTicker, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStockDataList, response.getBody());
        verify(financeService, times(1)).getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate));
    }



    @Test
    public void testRefreshCharts_ReturnsInternalServerError_WhenIOExceptionOccurs() throws IOException {
        // Arrange
        String stockTicker = "GOOGL";
        String startDate = "2023-06-01";
        String endDate = "2023-06-10";
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        when(financeService.getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate)))
                .thenThrow(new IOException());

        // Act
        ResponseEntity<List<StockData>> response = financeController.refreshCharts(stockTicker, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(financeService, times(1)).getStockData(eq(stockTicker), eq(startLocalDate), eq(endLocalDate));
    }
}
