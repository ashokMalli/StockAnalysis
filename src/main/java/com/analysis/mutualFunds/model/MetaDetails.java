package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MetaDetails {
    private String fund_house;
    private String scheme_type;
    private String scheme_category;
    private String scheme_code;
    private String scheme_name;
}
