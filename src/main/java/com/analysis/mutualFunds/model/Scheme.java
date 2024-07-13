package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Scheme {
    private String schemeCode;
    private String schemeName;
}
