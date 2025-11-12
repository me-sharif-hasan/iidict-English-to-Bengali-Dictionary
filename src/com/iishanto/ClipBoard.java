package com.iishanto;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClipBoard {
    String previous="";
    boolean first_cpy=true;
    public ClipBoard(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();
    }
    void init(){
        while (true){
            try {
                String text=getClipBoard();
                if(text.equals(previous)||text.equals("")) {
                    first_cpy=false;
                    continue;
                }
                if(first_cpy){
                    first_cpy=false;
                    previous=text;
                    continue;
                }
                Tools.getConfig().regNewText(text);
                Tools.getConfig().callEvent("new_text");
                previous=text;
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
