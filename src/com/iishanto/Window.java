package com.iishanto;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

        JTextField search=new JTextField();

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Tools.getConfig().regNewText(actionEvent.getActionCommand());
                Tools.getConfig().callEvent("new_text");
            }
        });


        search.setSize(300,30);
        jFrame.add(search);


        JTextArea jTextArea=new JTextArea();
        jTextArea.setText("Dictionary loading.\n");
        JScrollPane jScrollPane=new JScrollPane(jTextArea);
        jScrollPane.setBounds(0,30,300,300);
        Tools.getConfig().addEvent(new Event() {
            String prev="";
            @Override
            public void event() {
                if(prev.equals(Tools.getConfig().getLatestTranslation())){
                    return;
                }
                jTextArea.append(Tools.getConfig().getLatestTranslation());
                jScrollPane.validate();
                jFrame.validate();
                prev=Tools.getConfig().getLatestTranslation();
                jTextArea.setCaretPosition(jTextArea.getText().length() - 1);
                search.setText(prev.split(":")[0]);
            }
        });
        jTextArea.append("Dictionary has loaded successfully.\n");

        jFrame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                jScrollPane.setSize(jFrame.getWidth(),jFrame.getHeight()-70);
                jTextArea.setSize(jFrame.getWidth(),jFrame.getHeight()-70);
                search.setSize(jFrame.getWidth(),30);
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {

            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {

            }
        });

        jFrame.add(jScrollPane);
        jFrame.setVisible(true);
    }
}
