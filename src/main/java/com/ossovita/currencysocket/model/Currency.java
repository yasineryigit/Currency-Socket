package com.ossovita.currencysocket.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Currency {

    private String name;

    private String symbol;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;

    private BigDecimal highestPrice24h;

    private BigDecimal lowestPrice24h;

    private String changePercentage24h;

    private String time;


}
