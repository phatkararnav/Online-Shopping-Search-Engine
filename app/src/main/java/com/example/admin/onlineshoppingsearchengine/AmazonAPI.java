package com.example.admin.onlineshoppingsearchengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

public class AmazonAPI {

    private static final String ASSOCIATE_TAG = "keyurpotdar-21";
    private static final String ACCESS_KEY_ID = "AKIAIIB3O7G3TW72NAWA";
    private static final String SECRET_KEY = "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";

    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";
    private static final String ENDPOINT = "webservices.amazon.in";

    private static final String ERROR_NO_INTERNET_ACCESS = "Please check your internet connection.";
    private static final String ERROR_INTERNAL_PROBLEM = "Sorry! Some internal problem occurred.";

    private static Mac mac = null;
    private static JSONObject responseJSON = new JSONObject();
    @SuppressLint("StaticFieldLeak")
    private static Context context;


    AmazonAPI(Context c) {
        context = c;
        byte[] secretyKeyBytes;
        try {
            secretyKeyBytes = SECRET_KEY.getBytes(UTF8_CHARSET);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
            mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getResponse(String keywords) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("Service", "AWSECommerceService");
            params.put("Operation", "ItemSearch");
            params.put("Keywords", URLEncoder.encode(keywords, "UTF-8"));
            params.put("SearchIndex", "All");

            String url = getRequestURL(params);
            new GetResponse().execute(url, "allItems");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseJSON() {
        ArrayList<Item> itemArray = new ArrayList<>();
        try {
            // Errors
            if (responseJSON.has("customError")) {
                itemArray.add(new Item(responseJSON.getString("customError"), 0,
                        "", "", ""));
                GetResultsActivity.amazonItems = itemArray;
                GetResultsActivity gra = new GetResultsActivity();
                gra.displayResults(context);
                return;
            }

            JSONArray items = responseJSON.getJSONObject("ItemSearchResponse")
                    .getJSONObject("Items").getJSONArray("Item");

            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    String asin = item.getString("ASIN");

                    Map<String, String> newParams = new HashMap<>();
                    newParams.put("Service", "AWSECommerceService");
                    newParams.put("Operation", "ItemLookup");
                    newParams.put("ItemId", asin);
                    newParams.put("ResponseGroup", "ItemAttributes,Images");

                    String newUrl = getRequestURL(newParams);

                    JSONObject itemResponse = new GetItemDetails().execute(newUrl).get();
                    JSONObject itemDetails = itemResponse.getJSONObject("ItemLookupResponse")
                            .getJSONObject("Items").getJSONObject("Item");
                    JSONObject itemAttributes = itemDetails.getJSONObject("ItemAttributes");
                    String title = itemAttributes.getString("Title");
                    String itemUrl = itemDetails.getString("DetailPageURL");
                    String imageUrl = itemDetails.getJSONObject("SmallImage")
                            .getString("URL");
                    String price = itemDetails.getJSONObject("ItemAttributes")
                            .getJSONObject("ListPrice").getString("Amount");
                    double formattedPrice = Double.valueOf(price) / 100.0;

                    itemArray.add(new Item(title, formattedPrice, imageUrl,
                            "Amazon", itemUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            GetResultsActivity.amazonItems = itemArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        GetResultsActivity gra = new GetResultsActivity();
        gra.displayResults(context);
    }

    private static class GetResponse extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                URLConnection connection = new URL(url[0]).openConnection();
                InputStream response = connection.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                String xmlResponse = (s.hasNext() ? s.next() : "");
                Log.i("hello",xmlResponse);
                try {
                    responseJSON = XML.toJSONObject(xmlResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                try {
                    responseJSON.put("customError", ERROR_NO_INTERNET_ACCESS);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    responseJSON.put("customError", ERROR_INTERNAL_PROBLEM);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            parseJSON();
        }
    }

    private static class GetItemDetails extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... url) {
            JSONObject itemResponse = new JSONObject();
            try {
                URLConnection connection = new URL(url[0]).openConnection();
                InputStream response = connection.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                String xmlResponse = (s.hasNext() ? s.next() : "");

                try {
                    itemResponse = XML.toJSONObject(xmlResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return itemResponse;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                try {
                    itemResponse.put("customError_INTERNET_ERROR", ERROR_NO_INTERNET_ACCESS);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return itemResponse;
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    itemResponse.put("customError_INTERNAL_ERROR", ERROR_INTERNAL_PROBLEM);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return itemResponse;
            }
        }

        @Override
        protected void onPostExecute(JSONObject returnResponse) {
            super.onPostExecute(returnResponse);
        }
    }

    private static String getRequestURL(Map<String, String> params) {
        params.put("AWSAccessKeyId", ACCESS_KEY_ID);
        params.put("AssociateTag", ASSOCIATE_TAG);
        params.put("Timestamp", timestamp());

        SortedMap<String, String> sortedParamMap = new TreeMap<>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign = REQUEST_METHOD + "\n"
                      + ENDPOINT + "\n"
                      + REQUEST_URI + "\n"
                      + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);

        return "http://" + ENDPOINT + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;
    }

    private static String hmac(String stringToSign) {
        String signature;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature;
    }

    private static String timestamp() {
        String timestamp;
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DateFormat dfm =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("IST"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    private static String canonicalize(SortedMap<String, String> sortedParamMap){
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    private static String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }
}
