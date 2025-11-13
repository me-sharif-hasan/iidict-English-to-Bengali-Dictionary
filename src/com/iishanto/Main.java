package com.iishanto;

import javafx.application.Application;
import java.io.IOException;

public class Main {
    private static ClipBoard clipBoard;

    public static void main(String[] args) throws IOException, InterruptedException {
        clipBoard = new ClipBoard();
        Application.launch(WindowFX.class, args);
    }

    public static ClipBoard getClipBoard() {
        return clipBoard;
    }
}
