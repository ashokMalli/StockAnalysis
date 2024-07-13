package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class StocksData {
    private String company_name;
    private Double corpus_per;
}
