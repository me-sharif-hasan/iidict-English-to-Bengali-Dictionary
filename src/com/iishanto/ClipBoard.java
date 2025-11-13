package com.iishanto;

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
                } else if(first_cpy){
                    first_cpy=false;
                    previous=text;
                } else {
                    Tools.getConfig().regNewText(text);
                    Tools.getConfig().callEvent("new_text");
                    previous=text;
                }
                // Sleep 200ms between clipboard checks to avoid locking the clipboard
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Clipboard monitoring interrupted");
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
