package com.vianet.musicplayer.dataloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 9/22/2015.
 */
public class JsonParse2 {
    public static String[] ids;//title

//    public static ArrayList<String> name=new ArrayList<String>();


    private String json;

    public JsonParse2(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);

            JSONArray users = jsonObject.getJSONArray("report");

            ids = new String[users.length()];


            for(int i = 0; i< users.length(); i++){
                JSONObject jo = users.getJSONObject(i);

                ids[i] = jo.getString("welcometext");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}