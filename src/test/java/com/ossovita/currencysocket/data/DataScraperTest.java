package com.ossovita.currencysocket.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DataScraperTest {


    @Test
    void listenDataFromWebsite() {

        try {
            String url = "https://kur.doviz.com/";

            Document doc = Jsoup.connect(url).get();
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

                // Elde edilen verileri yazdırın
                System.out.println("Döviz Adı: " + name);
                System.out.println("Döviz Kodu: " + symbol);
                System.out.println("Alış Fiyatı: " + bidPrice);
                System.out.println("Satış Fiyatı: " + askPrice);
                System.out.println("Değer 1: " + highestPrice24h);
                System.out.println("Değer 2: " + lowestPrice24h);
                System.out.println("Değişim Yüzdesi: " + changePercentage24h);
                System.out.println("Zaman: " + time);
                System.out.println("-------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}