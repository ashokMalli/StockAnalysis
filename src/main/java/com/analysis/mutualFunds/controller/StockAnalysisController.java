package com.analysis.mutualFunds.controller;

import com.analysis.mutualFunds.model.FundData;
import com.analysis.mutualFunds.model.NavData;
import com.analysis.mutualFunds.service.FundService;
import com.analysis.mutualFunds.service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController

public class StockAnalysisController {

    @Autowired
    private final FundService fundService;

    @Autowired
    private HoldingService holdingService;

    public StockAnalysisController(FundService fundService) {
        this.fundService = fundService;
    }

    @GetMapping("/holdingsAccumulator")
    public Map<String,Double> getFundHoldings(@RequestParam(name = "schemeNames")String schemeNames){
        return holdingService.getHoldingDataWithAccumulated(schemeNames);
    }

    @GetMapping("/holdings")
    public Map<String,Double> getFundHolding(@RequestParam(name = "schemeCategoryFilter", required = false) String schemeCategoryFilter){
        List<FundData> filteredFundDataList = fundService.getFundDataList().stream()
                .filter(fundData -> ((schemeCategoryFilter == null) || (fundData.getSchemeCategory().toLowerCase().contains(schemeCategoryFilter)))).toList();
        return holdingService.getHoldingData(filteredFundDataList);
    }

    @GetMapping("/")
    public List<String> getMutualFundData(@RequestParam(name = "fundHouseFilter", required = false) String fundHouseFilter,
                                          @RequestParam(name = "schemeCategoryFilter", required = false) String schemeCategoryFilter,
                                          @RequestParam(name = "schemeCategoryDebtFundFilter", required = false) String schemeCategoryDebtFundFilter,
                                          @RequestParam(name = "trend") String trend,
                                          @RequestParam(name = "trendIndex") int trendIndex,
                                          @RequestParam(name = "schemeTypeFilter", required = false) String schemeTypeFilter,
                                          @RequestParam(name = "regularFundFilter", required = false) String regularFundFilter) {
        List<FundData> filteredFundDataList = fundService.getFundDataList().stream()
                .filter(fundData -> fundData.getNavDataList().size() > trendIndex)
                .filter(fundData -> ((schemeCategoryFilter == null) || (fundData.getSchemeCategory().equalsIgnoreCase(schemeCategoryFilter)))
                        && ((fundHouseFilter == null) || (fundData.getFundHouse().equalsIgnoreCase(fundHouseFilter)))
                        && ((regularFundFilter == null) || !(fundData.getSchemeName().toLowerCase().contains("regular")))
                        && ((schemeCategoryDebtFundFilter == null) || !(fundData.getSchemeCategory().toLowerCase().contains("debt")))
                        && ((schemeTypeFilter == null) || (fundData.getSchemeType().equalsIgnoreCase(schemeTypeFilter)))).toList();

        List<FundData> finalFundList = filteredFundDataList.stream().filter(fundData -> {
            List<Double> navList = fundData.getNavDataList().stream().map(NavData::getNav).map(Double::parseDouble).toList();
            Double trendValue = navList.get(trendIndex);
            System.out.println("trendValue" + trendValue);
            Double currentValue = navList.get(0);
            System.out.println("currentValue" + currentValue);
            if (trend.equalsIgnoreCase("high")) {
                return currentValue > trendValue;
            }
            return trendValue > currentValue;
        }).toList();


        return finalFundList.stream().sorted(Comparator.comparingDouble(fundData -> Double.parseDouble(fundData.getNavDataList().get(0).getNav()))).map(fundData -> fundData.getSchemeCode() + fundData.getSchemeName()).toList();
    }


}
