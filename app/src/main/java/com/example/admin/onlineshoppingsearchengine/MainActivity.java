package com.example.admin.onlineshoppingsearchengine;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PROGESS_BAR_TEXT = "Fetching items, please wait...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        TextView progressBar = findViewById(R.id.progress_bar);
        progressBar.setText("");
        super.onResume();
    }

    public boolean exit = false;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        Toast.makeText(getApplicationContext(),"Application exited Successfully!!!",Toast.LENGTH_SHORT).show();

    }

    public void search(View view) {
        Intent intent = new Intent(this, GetResultsActivity.class);
        EditText searchText = findViewById(R.id.search_text);
        String searchKey = searchText.getText().toString();
        if (searchKey.isEmpty() || searchKey.trim().equals("")) {
            return;
        }
        TextView progressBar = findViewById(R.id.progress_bar);
        progressBar.setText(PROGESS_BAR_TEXT);
        intent.putExtra("searchKey", searchKey);
        startActivity(intent);
    }

}
