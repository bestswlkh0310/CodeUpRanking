package com.bestswlkh0310.codeupranking.util;

import com.bestswlkh0310.codeupranking.rank.Rank;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CodeUpCrawler {
    private String BASE_URL = "https://codeup.kr/";
    private String RANK_URL = BASE_URL + "ranklist_1.php?school=724";

    public List<Rank> get() throws IOException, ParseException {
        Document doc = Jsoup.connect(RANK_URL).get();
        Elements ranks = doc.select("#ranklist");
        Elements rows = ranks.select("tr");
        return getData(rows);
    }

    private List<Rank> getData(Elements rows) throws IOException, ParseException {
        List<Rank> ranks = new ArrayList<>();
        for (Element row : rows) {
            Elements userNickName = row.select(".text-center");
            Elements user = row.getAllElements();

            Document userDoc = Jsoup.connect(BASE_URL + user.select("a").attr("href")).get();
            String scriptContent = getScript(userDoc);

            String userData1 = findData(scriptContent, "['정확한 풀이", "]);");
            String userData = findData(scriptContent, "['날짜',", "]);");
            if (userData1 != null && userData != null) {
                ranks.add(new Rank(
                        userNickName.get(0).text(),
                        user.get(1).text(),
                        convertToDictionary(userData1),
                        cleanData(userData)
                    )
                );
            }
        }
        return ranks;
    }

    private String getScript(Document document) {
        Elements scripts = document.select("script");
        StringBuilder scriptContent = new StringBuilder();
        for (Element script : scripts) {
            scriptContent.append(script.html());
        }
        return scriptContent.toString();
    }

    private String findData(String str, String str1, String str2) {
        int startIdx = str.indexOf(str1);
        int endIdx = str.indexOf(str2, startIdx);
        if (startIdx < 0 || endIdx < 0) return null;
        return str.substring(startIdx, endIdx).trim();
    }

    public List<List<Object>> cleanData(String input) throws ParseException {
        List<List<Object>> convertedData = new ArrayList<>();
        String[] entries = input.split("\\],");
        boolean t = true;
        for (String entry : entries) {
            if (t) {
                t = false;
                continue;
            }
            entry = entry.replaceAll("\\[|'|\\s", "");
            String[] values = entry.split(",");
            String dateString = values[0];
            int n1 = Integer.parseInt(values[1]);
            int n2 = Integer.parseInt(values[2]);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
            Date date = dateFormat.parse(dateString);
            long timestamp = date.getTime();

            Date date1 = new Date(timestamp);
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy년 MM월 dd일");

            List<Object> data = new ArrayList<>();
            data.add(dateFormat1.format(date1));
            data.add(n1);
            data.add(n2);
            convertedData.add(data);
        }
        return convertedData;
    }

    public Map<String, Integer> convertToDictionary(String input) {
        Map<String, Integer> dictionary = new HashMap<>();
        String[] parts = input.split("\\], \\[");
        for (String part : parts) {
            String[] keyValue = part.replaceAll("[\\[\\]']", "").split(", ");
            String key = keyValue[0];
            int value = Integer.parseInt(keyValue[1]);
            dictionary.put(key, value);
        }
        return dictionary;
    }
}
