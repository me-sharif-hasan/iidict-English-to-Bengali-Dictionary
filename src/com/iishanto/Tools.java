package com.iishanto;

import java.io.InputStream;
import java.util.*;

public class Tools {
    private static Tools tools=null;
    private final List<String> list=new ArrayList<>();
    private final List<Event> events=new ArrayList<>();
    String latest_translation="";
    String latest_source="";  // Store source text separately

    // Language settings
    private String sourceLanguage = "auto";
    private String targetLanguage = "bn";

    // Callback for translation start
    private Event translationStartCallback = null;

    Tools(){
        System.out.println("Translator initialized with Google Translate API");
    }

    public void setSourceLanguage(String lang) {
        this.sourceLanguage = lang;
    }

    public void setTargetLanguage(String lang) {
        this.targetLanguage = lang;
    }

    public void setTranslationStartCallback(Event callback) {
        this.translationStartCallback = callback;
    }

    public InputStream getRes(String file){
        // Remove leading slash if present
        String resourcePath = file.startsWith("/") ? file.substring(1) : file;
        // Try with res/ prefix
        InputStream is = Tools.class.getClassLoader().getResourceAsStream("res/" + resourcePath);
        if (is == null) {
            // Try without res/ prefix
            is = Tools.class.getClassLoader().getResourceAsStream(resourcePath);
        }
        if (is == null) {
            // Try with leading slash (original method)
            is = Tools.class.getResourceAsStream(file);
        }
        return is;
    }

    String meaning(String upword){
        List<String> theSentence=new ArrayList<>();
        theSentence.add(upword);
        try{
            // Use Google Translate with the selected languages
            List<String> translations=GoogleTranslateClient.getInstance().translateList(theSentence, sourceLanguage, targetLanguage);
            if(translations.isEmpty()){
                return "Translation not available!";
            }
            // Decode HTML entities in the translation result
            return decodeHtmlEntities(translations.get(0));
        }catch(Exception e){
            return "Translation error: " + e.getMessage();
        }
    }

    public void callEvent(String type){
        if(type.equals("new_text")){
            latest_translation="";
            latest_source="";
            String word= list.get(list.size() - 1);
            word=word.trim();
            // Decode HTML entities in source text
            latest_source = decodeHtmlEntities(word);  // Store source separately

            // Notify translation started
            if (translationStartCallback != null) {
                translationStartCallback.event();
            }

            // Run translation in background thread
            final String textToTranslate = word;
            new Thread(() -> {
                try {
                    latest_translation = meaning(textToTranslate);  // Store translation only (already decoded in meaning())
                    if(latest_translation.isEmpty()) return;
                    for(Event evt:events){
                        evt.event();
                    }
                } catch (Exception e) {
                    System.err.println("Translation error: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public String getLatestTranslation(){
        return latest_translation;
    }

    public String getLatestSource(){
        return latest_source;
    }

    public String getLatestTranslationFormatted(){
        return latest_source + ": " + latest_translation + "\n";
    }

    public void regNewText(String s){
        list.add(s);
        if(list.size()>10){
            list.remove(0);
        }
    }

    public void addEvent(Event evt){
        events.add(evt);
    }

    public static Tools getConfig(){
        if(tools==null){
            tools=new Tools();
        }
        return tools;
    }

    // Decode HTML entities to proper characters
    public static String decodeHtmlEntities(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Decode common HTML entities
        text = text.replace("&quot;", "\"");
        text = text.replace("&apos;", "'");
        text = text.replace("&lt;", "<");
        text = text.replace("&gt;", ">");
        text = text.replace("&amp;", "&");  // Must be last to avoid double-decoding
        text = text.replace("&#39;", "'");
        text = text.replace("&#34;", "\"");
        text = text.replace("&nbsp;", " ");

        // Decode numeric character references (e.g., &#8220; &#8221;)
        while (text.contains("&#")) {
            int start = text.indexOf("&#");
            int end = text.indexOf(";", start);
            if (end > start) {
                try {
                    String numStr = text.substring(start + 2, end);
                    int charCode;
                    if (numStr.startsWith("x") || numStr.startsWith("X")) {
                        // Hexadecimal
                        charCode = Integer.parseInt(numStr.substring(1), 16);
                    } else {
                        // Decimal
                        charCode = Integer.parseInt(numStr);
                    }
                    char decodedChar = (char) charCode;
                    text = text.substring(0, start) + decodedChar + text.substring(end + 1);
                } catch (NumberFormatException e) {
                    break; // Invalid format, stop trying
                }
            } else {
                break;
            }
        }

        return text;
    }
}
