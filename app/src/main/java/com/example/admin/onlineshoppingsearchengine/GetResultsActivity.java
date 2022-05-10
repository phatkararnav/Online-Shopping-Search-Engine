package com.example.admin.onlineshoppingsearchengine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class GetResultsActivity extends AppCompatActivity {

    private static final String ERROR_NO_INTERNET_ACCESS = "Please check your internet connection.";
    private static final String ERROR_INTERNAL_PROBLEM = "Sorry! Some internal problem occurred.";
    private static final String FETCHING_RESULTS = "Fetching results, please wait...";
    private static final int MAX_ITEMS = 50;

    public static ArrayList<Item> ebayItems;
    public static ArrayList<Item> amazonItems;
    public static ArrayList<Item> combinedItems;

    private TextView informationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_results);

        ebayItems = new ArrayList<>();
        amazonItems = new ArrayList<>();
        combinedItems = new ArrayList<>();

        Intent prevIntent = getIntent();
        String keywords = prevIntent.getStringExtra("searchKey");

        informationText = findViewById(R.id.informationText);
        informationText.setText(FETCHING_RESULTS);

        EbayAPI.getResponse(keywords);

        AmazonAPI amazonAPI = new AmazonAPI(getApplicationContext());
        amazonAPI.getResponse(keywords);
    }

    public void displayResults(Context context) {
        if (ebayItems.size() > 0) {
            switch (ebayItems.get(0).getTitle()) {
                case ERROR_NO_INTERNET_ACCESS:
//                    informationText.setText(ERROR_NO_INTERNET_ACCESS);
                    break;
                case ERROR_INTERNAL_PROBLEM:
                    break;
                default:
                    combinedItems.addAll(ebayItems);
                    break;
            }
        }

        if (amazonItems.size() > 0) {
            switch (amazonItems.get(0).getTitle()) {
                case ERROR_NO_INTERNET_ACCESS:
//                    informationText.setText(ERROR_NO_INTERNET_ACCESS);
                    return;
                case ERROR_INTERNAL_PROBLEM:
                    return;
                default:
                    combinedItems.addAll(amazonItems);
                    break;
            }
        }

        if (combinedItems.size() > 0) {
            // Sort items w.r.t price
            Collections.sort(combinedItems);

            if (combinedItems.size() > MAX_ITEMS) {
                combinedItems = new ArrayList<>(combinedItems.subList(0, MAX_ITEMS));
            }

            Intent intent = new Intent(context, DisplayResultsActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("itemArray", combinedItems);
            intent.putExtra("bundle", args);
            context.startActivity(intent);

        } else {
            String s = "No results found.";
            informationText.setText(s);
        }
    }
}
