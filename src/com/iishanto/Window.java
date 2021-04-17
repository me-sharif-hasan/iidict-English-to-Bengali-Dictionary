package com.iishanto;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class Window {
    public Window(){
        JFrame jFrame=new JFrame("iishanto.com - E2B light dict!");
        try {
            jFrame.setIconImage(ImageIO.read(Tools.getConfig().getRes("/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(300,340);
        jFrame.setLayout(null);
        jFrame.setAlwaysOnTop(true);


        JTextArea jTextArea=new JTextArea();
        jTextArea.setBounds(0,0,300,300);
        JScrollPane jScrollPane=new JScrollPane(jTextArea);
        jScrollPane.setBounds(0,0,300,300);
        Tools.getConfig().addEvent(new Event() {
            @Override
            public void event() {
                jTextArea.append(Tools.getConfig().getLatestTranslation()+"\n");
                jScrollPane.validate();
                jTextArea.setCaretPosition(jTextArea.getText().length() - 1);
                System.out.println("Window called");
            }
        });

        jFrame.add(jScrollPane);
        jFrame.setVisible(true);
    }
}
