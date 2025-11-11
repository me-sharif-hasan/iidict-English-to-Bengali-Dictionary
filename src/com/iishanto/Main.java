package com.iishanto;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        GoogleTranslateClient googleTranslateClient=GoogleTranslateClient.getInstance();
        List<String> words=List.of("Hello","World","How are you?");
        List<String> data = googleTranslateClient.translateList(words,"en","bn");
        new Window();
        new ClipBoard();
    }
}
