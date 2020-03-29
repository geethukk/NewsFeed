package com.moengage.newsfeed.Parsing;

import android.app.Activity;
import android.util.Log;

import com.moengage.newsfeed.Constants.AndyConstants;
import com.moengage.newsfeed.Model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseContent {

    private final String KEY_SUCCESS = "status";
    private final String KEY_MSG = "message";
    private final String NO_DATA = "No data";

    private Activity activity;

    public ParseContent(Activity activity) {
        this.activity = activity;
    }

    //checking response code is HTTP.Ok
    public boolean isSuccess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString(KEY_SUCCESS).equals(AndyConstants.Params.HTTPOK)) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //get error code
    public String getErrorCode(String response) {
        // Getting JSON Object
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString(KEY_MSG);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return NO_DATA;
    }

    //get news list
    public List<News> getInfo(String response) {
        List<News> newsModelArrayList = new ArrayList<News>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_SUCCESS).equals(AndyConstants.Params.HTTPOK)) {
                // Getting JSON Array node
                JSONArray dataArray = jsonObject.getJSONArray(AndyConstants.Params.ARTICLES);
                // looping through Array
                for (int i = 0; i < dataArray.length(); i++) {
                    News newsModel = new News();
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    newsModel.setAuthor(dataobj.getString(AndyConstants.Params.AUTHOR));
                    newsModel.setTitle(dataobj.getString(AndyConstants.Params.TITLE));
                    newsModel.setUrlToImage(dataobj.getString(AndyConstants.Params.URLTOIMAGE));
                    newsModel.setUrl(dataobj.getString(AndyConstants.Params.URL));
                    newsModel.setPublishedAt(dataobj.getString(AndyConstants.Params.PUBLISHEDAT));
                    newsModel.setDescription(dataobj.getString(AndyConstants.Params.DESCRIPTION));
                    // adding news to newsModelArrayList
                    newsModelArrayList.add(newsModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsModelArrayList;
    }
}