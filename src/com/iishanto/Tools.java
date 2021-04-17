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

    public void callEvent(String type){
        if(type.equals("new_text")){
            String word=(String) list.get(list.size() - 1);
            String upword=clean(word.toUpperCase(Locale.ROOT));
            if(hashMap.containsKey(upword)){
                System.out.println(word+":"+hashMap.get(upword));
                latest_translation=word+": "+hashMap.get(upword);
            }else{
                System.err.println(word+" not found in the database");
                latest_translation=word+": ডেটাবেসে নেই!";
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
        int i=0;
        String s="";
        while(i<word.length()){
            if(!(word.charAt(i)>='A'&&word.charAt(i)<='Z')) {
                i++;
            }else{
                break;
            }
        }
        while (i<word.length()){
            s+=word.charAt(i);
            i++;
        }
        word=s;
        s="";
        i=word.length()-1;
        while(i>=0){
            if(!(word.charAt(i)>='A'&&word.charAt(i)<='Z')){
                i--;
            }else{
                break;
            }
        }
        int j=0;
        while(j<=i){
            s+=word.charAt(j);
            j++;
        }
        return s;
    }

    public static Tools getConfig(){
        if(tools==null){
            tools=new Tools();
        }
        return tools;
    }
}
