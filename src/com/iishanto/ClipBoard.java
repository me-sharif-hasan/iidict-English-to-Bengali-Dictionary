package com.iishanto;

import javafx.application.Platform;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.concurrent.TimeUnit;

public class ClipBoard {
    String previous="";
    boolean first_cpy=true;
    private volatile boolean running = true;
    private final Thread monitorThread;

    public ClipBoard(){
        monitorThread = new Thread(this::init);
        monitorThread.setDaemon(true); // Make it a daemon thread so it doesn't prevent JVM shutdown
        monitorThread.setName("ClipboardMonitor");
        monitorThread.start();
    }

    public void stop() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }

    void init(){
        while (running){
            try {
                String text=getClipBoard();
                if(text.equals(previous)|| text.isEmpty()) {
                    first_cpy=false;
                    continue;
                }
                if(first_cpy){
                    first_cpy=false;
                    previous=text;
                    continue;
                }
                Tools.getConfig().regNewText(text);
                // Use Platform.runLater() to update JavaFX UI on the JavaFX Application Thread
                if (Platform.isFxApplicationThread()) {
                    Tools.getConfig().callEvent("new_text");
                } else {
                    Platform.runLater(() -> Tools.getConfig().callEvent("new_text"));
                }
                previous=text;
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                // Thread interrupted, exit gracefully
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Log other exceptions but continue running
                System.err.println("Clipboard monitor error: " + e.getMessage());
            }
        }
        System.out.println("Clipboard monitoring stopped");
    }

    public String getClipBoard(){
        try {
            String data=(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            // Decode HTML entities from clipboard text
            return Tools.decodeHtmlEntities(data.trim());
        } catch (Exception e){
            return "";
        }
    }
}
