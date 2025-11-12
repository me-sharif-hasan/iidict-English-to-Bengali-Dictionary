package com.iishanto;

import javafx.application.Application;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize Google Translate client
        GoogleTranslateClient googleTranslateClient = GoogleTranslateClient.getInstance();

        // Start clipboard monitoring
        new ClipBoard();

        // Launch JavaFX application
        Application.launch(WindowFX.class, args);
    }
}
