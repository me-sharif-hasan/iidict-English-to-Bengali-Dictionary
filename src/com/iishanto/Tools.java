package com.iishanto;

import javax.tools.Tool;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class Tools {
    private static Tools tools=null;
    private List list=new ArrayList<String>();
    private HashMap hashMap=new HashMap<String,String>();
    private List events=new ArrayList<Event>();
    String latest_translation="";

    static boolean loading=false;

    Tools(){
        if(loading) return;
        loading=true;
        System.out.println("Loading......");

        InputStream getDict=getDict=getRes("/db.csv");
        Scanner scanner=new Scanner(getDict);
        while (scanner.hasNextLine()){
            String ln=scanner.nextLine();
            String []lw=ln.split("\\|");
            if(lw.length<2){
                System.err.println("error: "+ln);
                continue;
            }
            hashMap.put(lw[0],lw[1]);
        }
        System.out.println("Loading has successfully completed!");
        loading=false;
    }
    public InputStream getRes(String file){
        InputStream inputStream=Tools.class.getResourceAsStream(file);
        return inputStream;
    }

    String meaning(String upword){
        upword=upword.toUpperCase(Locale.ROOT);
        if(hashMap.containsKey(upword)){
            return (String) hashMap.get(upword);
        }else{
            return "ডেটাবেসে নেই!";
        }
    }

    public void callEvent(String type){
        if(type.equals("new_text")){
            latest_translation="";
            String word=(String) list.get(list.size() - 1);
            word=word.trim();
            String []words=word.split(" ");
            System.out.println(word+":"+words.length);
            for(String ws:words){
                String s=clean(ws);
                System.out.println(s);
                latest_translation+=s+": "+meaning(s)+"\n";
            }
            for(Object evt:events){
                ((Event)evt).event();
            }
        }
    }
    public String getLatestTranslation(){
        return latest_translation;
    }
    public void regNewText(String s){
        list.add(s);
        if(list.size()>10){
            list.remove(0);
        }
    }

    boolean valid_word(String word){
        if(word.length()==0) return false;
        word=word.toUpperCase(Locale.ROOT);
        if(word.charAt(0)>='A'&&word.charAt(0)<='Z'&&word.charAt(word.length()-1)>='A'&&word.charAt(word.length()-1)<='Z'){
            return true;
        }
        return false;
    }

    public void addEvent(Event evt){
        events.add(evt);
    }

    private String clean(String word){
        String s="";
        String u_word=word.toUpperCase(Locale.ROOT);

        int i=0,j=u_word.length()-1;
        int dist=j-i;
        while (i<=j){
            if(!(u_word.charAt(i)>='A'&&u_word.charAt(i)<='Z')){
                i++;
            }
            if(!(u_word.charAt(j)>='A'&&u_word.charAt(i)<='j')){
                j--;
            }
            if(dist==j-i){
                break;
            }
            dist=j-i;
        }
        while (i<=j){
            s=s+word.charAt(i);
            i++;
        }
        System.out.println("Cleaned text: "+s);
        return s;
    }

    public static Tools getConfig(){
        if(tools==null){
            tools=new Tools();
        }
        return tools;
    }
}
