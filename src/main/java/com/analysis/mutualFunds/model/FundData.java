package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FundData {
    private String fundHouse;
    private String schemeType;
    private String schemeCategory;
    private String schemeCode;
    private String schemeName;
    private List<NavData> navDataList;
}
