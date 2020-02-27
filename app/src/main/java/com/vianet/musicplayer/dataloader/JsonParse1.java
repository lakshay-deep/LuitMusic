package com.vianet.musicplayer.dataloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 9/22/2015.
 */
public class JsonParse1 {
    public static String[] file;//title
    public static String[] artist;//content
    public static String[] musi;//image url
    public static String[] producer;//video url
    public static String[] title;//image url
    public static String[] album;//video url
    public static String[] thumbnail;//image url

    public static String[] date;//Date url

//    public static ArrayList<String> name=new ArrayList<String>();

    private static final String JSON_ARRAY = "report";

    private String json;

    public JsonParse1(String json) {
        this.json = json;
    }

    public void parseJSON() {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
            JSONArray users = jsonObject.getJSONArray(JSON_ARRAY);

            file = new String[users.length()];
            artist = new String[users.length()];
            musi = new String[users.length()];
            producer = new String[users.length()];
            title = new String[users.length()];
            album = new String[users.length()];
            thumbnail = new String[users.length()];

            date = new String[users.length()];
            for (int i = 0; i < users.length(); i++) {
                JSONObject jo = users.getJSONObject(i);
                file[i] = "http://104.251.217.194/luit/" + jo.getString("mp3File");
                artist[i] = jo.getString("artist");
                musi[i] = jo.getString("musicDirectory");
                producer[i] = jo.getString("producer");
                title[i] = jo.getString("mp3Title");
                album[i] = jo.getString("album");
                thumbnail[i] = "http://104.251.217.194/luit/" + jo.getString("thumbnail");

                // String datel = jo.getString(KEY_Date);


//                        datel.substring(0,11);
//                name.add(emails[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}