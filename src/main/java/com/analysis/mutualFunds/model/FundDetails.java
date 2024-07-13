package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FundDetails {
    private MetaDetails meta;
    private List<NavData> data;
    private String status;
}
