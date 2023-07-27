package com.ossovita.currencysocket.data;

import com.ossovita.currencysocket.model.Currency;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
            // Tabloyu id'sine göre seçin
            Element table = doc.select("#currencies").first();

            // Tabloya ait tbody elementini alın
            Element tbody = table.select("tbody").first();

            // Tbody içindeki tüm tr satırlarını seçin
            Elements rows = tbody.select("tr");

            // Her bir tr satırını döngüyle ayrıştırın
            for (Element row : rows) {
                if (row.text().isEmpty()) {
                    continue; // Boş satırı atlayarak bir sonraki satıra geç
                }
                // Currency name ve diğer değerleri tutacak değişkenleri tanımlayın
                String name = "";
                String symbol = "";
                String bidPrice = "";
                String askPrice = "";
                String highestPrice24h = "";
                String lowestPrice24h = "";
                String changePercentage24h = "";
                String time = "";

                // Currency name ve currency code'ları içeren div elementini seçin
                Element currencyDiv = row.selectFirst("div.currency-details");
                name = currencyDiv.selectFirst("div.cname").text().trim();
                symbol = currencyDiv.select("div").first().text().trim().split("\\s+")[0];

                // Alış ve satış fiyatlarını alın
                bidPrice = row.select("td.text-bold[data-socket-key=" + symbol + "][data-socket-attr=bid]").text().trim();
                askPrice = row.select("td.text-bold[data-socket-key=" + symbol + "][data-socket-attr=ask]").text().trim();

                // Diğer bilgileri alın
                Elements otherElements = row.select("td.text-gray");
                highestPrice24h = otherElements.get(0).text().trim();
                lowestPrice24h = otherElements.get(1).text().trim();

                // Değişim yüzdesini alın
                changePercentage24h = row.select("td.text-bold.change[data-socket-key=" + symbol + "][data-socket-attr=c]").text().trim();

                // Zamanı alın
                time = row.select("td.time").text().trim();

                Currency currency = Currency.builder()
                        .name(name)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(bidPrice.replace(",",".")))
                        .askPrice(new BigDecimal(askPrice.replace(",",".")))
                        .highestPrice24h(new BigDecimal(highestPrice24h.replace(",",".")))
                        .lowestPrice24h(new BigDecimal(lowestPrice24h.replace(",",".")))
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
