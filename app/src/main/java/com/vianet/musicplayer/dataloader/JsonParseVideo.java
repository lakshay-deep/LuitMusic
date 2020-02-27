package com.vianet.musicplayer.dataloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by editing2 on 10-Jan-17.
 */

public class JsonParseVideo {
    public static String[] ytId;       //youtube id
    public static String[] artist;     //artist name
    public static String[] description;//video description
    public static String[] title;      //video title
    public static String[] album;      //video album


    public static final String JSON_ARRAY = "report";

    private JSONArray users = null;

    private String json;

    public JsonParseVideo(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);

            ytId = new String[users.length()];
            artist = new String[users.length()];
            description = new String[users.length()];
            title = new String[users.length()];
            album = new String[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                ytId[i] = jo.getString("ytID");
                artist[i] = jo.getString("artist");
                description[i] = jo.getString("description");
                title[i] = jo.getString("title");
                album[i] = jo.getString("album");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
