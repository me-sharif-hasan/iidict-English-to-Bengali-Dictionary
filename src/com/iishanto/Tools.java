package com.iishanto;

import java.io.InputStream;
import java.util.*;

public class Tools {
    private static Tools tools=null;
    private List<String> list=new ArrayList<String>();
    private HashMap<String, String> hashMap=new HashMap<String,String>();
    private List<Event> events=new ArrayList<Event>();
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
                continue;
            }
            hashMap.put(lw[0],lw[1]);
        }
        System.out.println("Loading has successfully completed!");
        loading=false;
    }
    public InputStream getRes(String file){
        return Tools.class.getResourceAsStream(file);
    }

    String meaning(String upword){
        List<String> theSentence=new ArrayList<String>();
        theSentence.add(upword);
        try{
            List<String> translations=GoogleTranslateClient.getInstance().translateList(theSentence,"ja","bn");
            if(translations.isEmpty()){
                return "ডেটাবেসে নেই!";
            }
            return translations.get(0);
        }catch(Exception e){
            return "ডেটাবেসে নেই!";
        }
//        upword=upword.toUpperCase(Locale.ROOT);
//        return hashMap.getOrDefault(upword, "ডেটাবেসে নেই!");
    }

    public void callEvent(String type){
        if(type.equals("new_text")){
            latest_translation="";
            String word= list.get(list.size() - 1);
            word=word.trim();
            latest_translation = word + ": " + meaning(word) + "\n";
            if(latest_translation.equals("")) return;
            for(Event evt:events){
                evt.event();
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
            if(!(u_word.charAt(j)>='A'&&u_word.charAt(j)<='Z')){
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
        return s;
    }

    public static Tools getConfig(){
        if(tools==null){
            tools=new Tools();
        }
        return tools;
    }
}
