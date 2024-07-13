package com.analysis.mutualFunds.service;

import com.analysis.mutualFunds.model.FundData;
import com.analysis.mutualFunds.model.FundDetails;
import com.analysis.mutualFunds.model.NavData;
import com.analysis.mutualFunds.model.Scheme;
import com.analysis.mutualFunds.util.FileUtil;
import com.analysis.mutualFunds.util.LastWorkingDay;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.analysis.mutualFunds.constants.Constant.baseURI;

@Service
public class FundService {

    @Autowired
    RestTemplate restTemplate;

    @Getter
    private List<FundData> fundDataList = new ArrayList<>();

    private final List<String> invalidSchemeCodeList = new ArrayList<>();

    private String lastWorkingDay = null;


    @PostConstruct
    public void fetchAndStoreFundsList() throws IOException {
        try {
            lastWorkingDay = LastWorkingDay.getLastWorkingDate();
            System.out.println("Calling mf api...................");
            Scheme[] schemeArray = restTemplate.getForObject(baseURI, Scheme[].class);
            assert schemeArray != null;
            List<String> invalidSchemeCodeList = FileUtil.readFromFile();
            Set<String> schemeCodeSet = Arrays.stream(schemeArray).map(Scheme::getSchemeCode).filter(schemeCode -> !invalidSchemeCodeList.contains(schemeCode)).collect(Collectors.toSet());
            System.out.println("Available Scheme count : " + schemeCodeSet.size());
            ExecutorService executorService = Executors.newFixedThreadPool(25);
            System.out.println("Retrieving Fund Data............");
            for (String schemeCode : schemeCodeSet) {
                System.out.println("Fetching the fund data for schemeCode " + schemeCode);
                // Future<FundData> fundDataFuture = executorService.submit(() -> getFundData(schemeCode));
                //  FundData fundData = fundDataFuture.get();
                //  if (fundData != null) {
                //       fundDataList.add(fundData);
                //  }
                executorService.submit(() -> {
                    FundData fundData;
                    try {
                        fundData = getFundData(schemeCode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (fundData != null) {
                        fundDataList.add(fundData);
                    }
                });
            }
            System.out.println("Fund data fetched and stored............");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.writeToFile(invalidSchemeCodeList);
        }

    }

    private FundData getFundData(String schemeCode) throws IOException {
        FundData fundData = null;
        FundDetails fundDetails = restTemplate.getForObject(baseURI + "/" + schemeCode, FundDetails.class);
        assert fundDetails != null;
        if (fundDetails.toString().contains(lastWorkingDay)) {
            fundData = new FundData();
            fundData.setFundHouse(fundDetails.getMeta().getFund_house());
            fundData.setSchemeCategory(fundDetails.getMeta().getScheme_category());
            fundData.setSchemeCode(fundDetails.getMeta().getScheme_code());
            fundData.setSchemeName(fundDetails.getMeta().getScheme_name());
            fundData.setSchemeType(fundDetails.getMeta().getScheme_type());
            List<NavData> navDataList = fundDetails.getData().stream().sorted(Comparator.comparing(
                    FundService::getParseStringToDate,
                    Comparator.reverseOrder()
            )).collect(Collectors.toList());
            fundData.setNavDataList(navDataList);
        } else {
            System.out.println(schemeCode);
            invalidSchemeCodeList.add(schemeCode);
        }
        return fundData;
    }

    private static LocalDate getParseStringToDate(NavData navData) {
        return LocalDate.parse(navData.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}
