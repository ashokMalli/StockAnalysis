package com.analysis.mutualFunds.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class NavData {
    private String date;
    private String nav;
}
