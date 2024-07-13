package com.analysis.mutualFunds.service;

import com.analysis.mutualFunds.model.FundData;
import com.analysis.mutualFunds.model.Holdings;
import com.analysis.mutualFunds.model.NavData;
import com.analysis.mutualFunds.model.StocksData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoldingService {
    @Autowired
    RestTemplate restTemplate;


    public Set<String> getHoldingData(List<FundData> fundDataList, String trend, int initialIndex) {
         return fundDataList.stream().filter(fundData -> {
            List<NavData> navDataList = fundData.getNavDataList();
            double currentValue = Double.parseDouble(navDataList.get(0).getNav());
            double initialValue = Double.parseDouble(navDataList.get(initialIndex).getNav());
           if(trend.equalsIgnoreCase("high")){
               return currentValue > initialValue;
           }
           return initialValue > currentValue;
        }).map(fundData -> {
             String schemeName = fundData.getSchemeName();
             String[] split = schemeName.split("-",2);
             return split[0];
         }).collect(Collectors.toSet());
    }

    public Map<String, Double>  getHoldingDataWithAccumulated(String schemeNames, Double amount) {
        String[] schemeNamesArray = schemeNames.split(",");
        Set<String> schemeSet = Arrays.stream(schemeNamesArray).collect(Collectors.toSet());
        return getHoldingsMap(schemeSet,amount);
    }

    private Map<String, Double> getHoldingsMap(Set<String> schemeSet, Double amount) {
        Map<String, Double> holdingsMap = new HashMap<>();
        Map<String, Double> holdingsPercentageMap = new HashMap<>();
        Double totalCorpus = 0.0;
        for (String schemeName : schemeSet) {
            try {
                String url = "https://groww.in/v1/api/data/mf/web/v3/scheme/search/" + schemeName;
                Holdings holdings = restTemplate.getForObject(url, Holdings.class);
                assert holdings != null;
                for (StocksData data : holdings.getHoldings()) {
                    String companyName = data.getCompany_name();
                    Double corpusPer = data.getCorpus_per();
                    totalCorpus = totalCorpus + corpusPer;
                    if (holdingsMap.containsKey(companyName)) {
                        holdingsMap.compute(companyName, (k, currCorpus) -> corpusPer + currCorpus);
                    } else {
                        holdingsMap.put(companyName, corpusPer);
                    }
                }
                for (Map.Entry<String, Double> holdingEntry : holdingsMap.entrySet()){
                    double holdingPercentage = (holdingEntry.getValue() / totalCorpus) * amount;
                    holdingsPercentageMap.put(holdingEntry.getKey(),holdingPercentage);
                }
            }catch(Exception e){
                System.out.println("Error in calling grow api for schemeName" + schemeName);
            }
        }
        return holdingsPercentageMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // In case of a merge (which won't happen here), keep the existing entry
                        LinkedHashMap::new // Preserve the order of elements
                ));
    }

}
