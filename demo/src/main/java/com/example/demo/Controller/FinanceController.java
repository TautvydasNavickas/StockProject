package com.example.demo.Controller;

import com.example.demo.Model.StockData;
import com.example.demo.Service.FinanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class FinanceController {
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/")
    public String showCharts() {
        return "charts";
    }

    @GetMapping("/refreshCharts")
    public ResponseEntity<List<StockData>> refreshCharts(@RequestParam("stockTicker") String stockTicker,
                                                         @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
                                                         @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startLocalDate = LocalDate.parse(startDate.trim(), dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDate.trim(), dateFormatter);

            List<StockData> stockDataList = financeService.getStockData(stockTicker, startLocalDate, endLocalDate);
            return ResponseEntity.ok(stockDataList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}





