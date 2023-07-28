package com.ossovita.currencysocket.data;

import com.ossovita.currencysocket.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DataScraper {

    @Value("${source-url}")
    private String sourceUrl;


    public List<Currency> getDataFromWebsite() {

        List<Currency> currencyList = new ArrayList<>();

        try {

            Document doc = Jsoup.connect(sourceUrl).get();
            //Select table depends on the id
            Element table = doc.select("#currencies").first();

            //Get tbody element in the table
            Element tbody = table.select("tbody").first();

            // Select all tr rows in the tbody
            Elements rows = tbody.select("tr");

            // Parse each tr line in a loop
            for (Element row : rows) {
                if (row.text().isEmpty()) {
                    continue; // skip useless rows
                }

                // get currency name and symbol
                Element currencyDiv = row.selectFirst("div.currency-details");
                String name = currencyDiv.selectFirst("div.cname").text().trim();
                String symbol = currencyDiv.select("div").first().text().trim().split("\\s+")[0];

                // get ask and bid prices
                String bidPrice = row.select("td.text-bold[data-socket-key=" + symbol + "][data-socket-attr=bid]").text().trim();
                String askPrice = row.select("td.text-bold[data-socket-key=" + symbol + "][data-socket-attr=ask]").text().trim();

                // get highest and lowest price for 24h timeline
                Elements otherElements = row.select("td.text-gray");
                String highestPrice24h = otherElements.get(0).text().trim();
                String lowestPrice24h = otherElements.get(1).text().trim();

                //get change percentage 24h
                String changePercentage24h = row.select("td.text-bold.change[data-socket-key=" + symbol + "][data-socket-attr=c]").text().trim();

                //get time
                String time = row.select("td.time").text().trim();

                Currency currency = Currency.builder()
                        .name(name)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(bidPrice.replace(",", ".")))
                        .askPrice(new BigDecimal(askPrice.replace(",", ".")))
                        .highestPrice24h(new BigDecimal(highestPrice24h.replace(",", ".")))
                        .lowestPrice24h(new BigDecimal(lowestPrice24h.replace(",", ".")))
                        .changePercentage24h(changePercentage24h)
                        .time(time)
                        .build();

                currencyList.add(currency);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Data scraped: " + LocalDateTime.now());

        return currencyList;
    }

}
