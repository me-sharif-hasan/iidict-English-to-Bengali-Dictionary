package com.iishanto;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Locale;

public class ClipBoard {
    String previous="";
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
            String text=getClipBoard();
            if(text.equals(previous)||text.equals("")) {
                continue;
            }
            Tools.getConfig().regNewText(text);
            Tools.getConfig().callEvent("new_text");
            previous=text;
        }
    }

    public String getClipBoard(){
        try {
            String data=(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return data.trim();
        } catch (Exception e){
            return "";
        }
    }
}
