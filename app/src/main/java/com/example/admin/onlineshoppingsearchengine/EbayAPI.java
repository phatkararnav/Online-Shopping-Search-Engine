package com.example.admin.onlineshoppingsearchengine;

import android.os.AsyncTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class EbayAPI {

    private static final String PROD_APP_ID = "KeyurPot-Androida-PRD-891c54ee4-3129e3e7";

    private static final String ERROR_NO_INTERNET_ACCESS = "Please check your internet connection.";
    private static final String ERROR_INTERNAL_PROBLEM = "Sorry! Some internal problem occurred.";

    private static JSONObject responseJSON = new JSONObject();

    public static void getResponse(String keywords) {
        String url = getRequestURL(keywords);
        new GetResponse().execute(url);
    }

    private static void parseJSON() {
        ArrayList<Item> itemArray = new ArrayList<>();
        // Errors
        if (responseJSON.containsKey("customError")) {
            itemArray.add(new Item((String) responseJSON.get("customError"), 0,
                    "", "", ""));
            GetResultsActivity.ebayItems = itemArray;
            return;
        }

        try {
            JSONObject searchResult = (JSONObject) ((JSONArray) ((JSONObject) ((JSONArray)
                    responseJSON.get("findItemsByKeywordsResponse")).get(0))
                    .get("searchResult")).get(0);

            String count = (String) searchResult.get("@count");
            if (count.equals("0")) {
                GetResultsActivity.ebayItems = itemArray;
            }
            JSONArray items = (JSONArray) searchResult.get("item");

            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                String title, imageUrl, itemUrl;
                float price;
                try {
                    title = (String) ((JSONArray) item.get("title")).get(0);
                } catch (Exception e) {
                    title = "";
                    e.printStackTrace();
                }
                try {
                    imageUrl = (String) ((JSONArray) item.get("galleryURL")).get(0);
                } catch (Exception e) {
                    imageUrl = "";
                    e.printStackTrace();
                }
                try {
                    itemUrl = (String) ((JSONArray) item.get("viewItemURL")).get(0);
                } catch (Exception e) {
                    itemUrl = "";
                    e.printStackTrace();
                }
                try {
                    JSONObject sellingStatus = (JSONObject) ((JSONArray)
                            item.get("sellingStatus")).get(0);
                    JSONObject currentPrice = (JSONObject) ((JSONArray)
                            sellingStatus.get("currentPrice")).get(0);
                    price = Float.valueOf((String) currentPrice.get("__value__"));
                    price *= 71.98;
                } catch (Exception e) {
                    price = -1;
                    e.printStackTrace();
                }

                itemArray.add(new Item(title, price, imageUrl, "Ebay", itemUrl));
            }

            GetResultsActivity.ebayItems = itemArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRequestURL(String keywords) {
        try {
            return "http://svcs.ebay.com/services/search/FindingService/v1?"
                 + "OPERATION-NAME=findItemsByKeywords"
                 + "&SERVICE-VERSION=1.0.0"
                 + "&SECURITY-APPNAME=" + PROD_APP_ID
                 + "&RESPONSE-DATA-FORMAT=JSON"
                 + "&REST-PAYLOAD"
                 + "&keywords=" + URLEncoder.encode(keywords, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static class GetResponse extends AsyncTask<String, Void, String> {

        @Override
        @SuppressWarnings("unchecked")
        protected String doInBackground(String... url) {
            try {
                URLConnection connection = new URL(url[0]).openConnection();
                InputStream response = connection.getInputStream();
                JSONParser jsonParser = new JSONParser();
                responseJSON = (JSONObject) jsonParser.parse(
                        new InputStreamReader(response, "UTF-8"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
                responseJSON.put("customError_INTERNET_ERROR", ERROR_NO_INTERNET_ACCESS);
            } catch (Exception e) {
                e.printStackTrace();
                responseJSON.put("customError_INTERNAL_ERROR", ERROR_INTERNAL_PROBLEM);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            parseJSON();
        }
    }
}
