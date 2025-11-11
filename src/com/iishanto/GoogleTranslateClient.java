package com.iishanto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.*;
import com.google.gson.*;

public class GoogleTranslateClient {
    private static final String API_KEY_SOURCE_LINK = "https://translate.google.com/translate_a/element.js?cb=gtElInit&hl=en-US&client=wt";
    private static final String API_HTML_TRANSLATE_URL = "https://translate-pa.googleapis.com/v1/translateHtml";
    private static GoogleTranslateClient instance = new GoogleTranslateClient();
    private static final Map<String, Map<String, String>> cache = new HashMap<>();

    private final HttpClient client = HttpClient.newHttpClient();
    private String token;

    private GoogleTranslateClient() {}

    public static GoogleTranslateClient getInstance() {
        return instance;
    }

    public String getTokenJs() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(API_KEY_SOURCE_LINK)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        String body = res.body();
        String url = getApiKeyJs(body);
        if (url != null) {
            return getApiKey(url);
        }
        return null;
    }

    public List<String> translateList(List<String> wordList, String sl, String tl) throws IOException, InterruptedException {
        if (token == null) token = getTokenJs();
        if (token != null) {
            Map<String, String> headers = Map.of(
                    "X-Goog-Api-Key", token,
                    "Content-Type", "application/json+protobuf"
            );

            List<String> wordList2 = new ArrayList<>();
            Map<Integer, Integer> idxs = new HashMap<>();
            int pos = 0;
            for (int i = 0; i < wordList.size(); i++) {
                Map<String, String> langCache = cache.get(tl);
                if (langCache == null || !langCache.containsKey(wordList.get(i))) {
                    idxs.put(i, pos++);
                    wordList2.add(wordList.get(i));
                }
            }

            List<String> data = new ArrayList<>();
            if (!wordList2.isEmpty()) {
                String body = new Gson().toJson(Arrays.asList(Arrays.asList(wordList2, sl, tl), "wt_lib"));
                HttpRequest req = HttpRequest.newBuilder(URI.create(API_HTML_TRANSLATE_URL))
                        .headers("X-Goog-Api-Key", token, "Content-Type", "application/json+protobuf")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                JsonArray json = JsonParser.parseString(res.body()).getAsJsonArray();
                JsonArray arr = json.get(0).getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) data.add(arr.get(i).getAsString());
            }

            if (!data.isEmpty() || !cache.isEmpty()) {
                List<String> finalData = new ArrayList<>();
                List<String> output = data;
                for (int i = 0; i < wordList.size(); i++) {
                    Integer idx = idxs.get(i);
                    if (idx != null && idx < output.size()) {
                        cache.computeIfAbsent(tl, k -> new HashMap<>());
                        String translated = output.get(idx);
                        cache.get(tl).put(wordList.get(i), translated);
                        finalData.add(translated);
                    } else {
                        finalData.add(cache.get(tl).get(wordList.get(i)));
                    }
                }
                return finalData;
            }
        }
        return null;
    }

    private String getApiKeyJs(String body) {
        Pattern p = Pattern.compile("_loadJs\\('([^']*)'\\)");
        Matcher m = p.matcher(body);
        if (m.find()) {
            String url = m.group(1);
            return decodeString(url);
        }
        return null;
    }

    private String decodeString(String input) {
        StringBuilder sb = new StringBuilder();
        Matcher m = Pattern.compile("\\\\x([0-9A-Fa-f]{2})").matcher(input);
        int lastEnd = 0;
        while (m.find()) {
            sb.append(input, lastEnd, m.start());
            int code = Integer.parseInt(m.group(1), 16);
            sb.append((char) code);
            lastEnd = m.end();
        }
        sb.append(input.substring(lastEnd));
        return sb.toString().replace("\\/", "/");
    }

    private String getApiKey(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return extractApiKey(res.body());
    }

    private String extractApiKey(String jsCode) {
        Pattern p = Pattern.compile("\"X-goog-api-key\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(jsCode);
        return m.find() ? m.group(1) : "";
    }

    public String predictPageLanguage() {
        return "";
    }
}
