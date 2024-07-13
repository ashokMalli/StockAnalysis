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
    private RestTemplate restTemplate;


    public Map<String, Double> getHoldingData(List<FundData> fundDataList) {
        return fundDataList.stream().filter(fundData -> {
            List<NavData> navDataList = fundData.getNavDataList();
            int size = navDataList.size();
            double currentValue = Double.parseDouble(navDataList.get(0).getNav());
            double initialValue = Double.parseDouble(navDataList.get(size - 1).getNav());
            double profit = currentValue - initialValue;
            double v = (profit * 100) / initialValue;
            return v > 50;
        }).collect(Collectors.toMap(FundData::getSchemeName, fundData -> Double.parseDouble(fundData.getNavDataList().get(0).getNav())));
    }

    public Map<String, Double>  getHoldingDataWithAccumulated(String schemeNames) {
        String[] schemeNamesArray = schemeNames.split(",");
        Set<String> schemeSet = Arrays.stream(schemeNamesArray).collect(Collectors.toSet());
        Map<String, Double> holdingsMap = new HashMap<>();
        for (String schemeName : schemeSet) {
            try {
                String url = "https://groww.in/v1/api/data/mf/web/v3/scheme/search/" + schemeName;
                Holdings holdings = restTemplate.getForObject(url, Holdings.class);
                assert holdings != null;
                for (StocksData data : holdings.getHoldings()) {
                    String companyName = data.getCompany_name();
                    Double corpusPer = data.getCorpus_per();
                    if (holdingsMap.containsKey(companyName)) {
                        holdingsMap.compute(companyName, (k, value) -> corpusPer + value);
                    } else {
                        holdingsMap.put(companyName, corpusPer);
                    }
                }
            }catch(Exception e){
                System.out.println("Error in calling grow api for schemeName" + schemeName);
            }
        }
        return holdingsMap.entrySet()
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
