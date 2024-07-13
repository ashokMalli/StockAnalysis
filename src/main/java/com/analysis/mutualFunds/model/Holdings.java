package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class Holdings {
    private List<StocksData> holdings;
}
