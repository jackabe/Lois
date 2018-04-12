package com.lois.jack.lois;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class LandingActivity extends AppCompatActivity {

    private ListView landingList;
    private LandingCustomAdapter landingCustomAdapter;
    private ArrayList<String> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        landingList = findViewById(R.id.landingList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        options = new ArrayList<>();
        options.add("Receipts");
        options.add("Discounts");
        options.add("Budget");

        int n = /* number of cells */ 3;
        int height = (landingList.getBottom() - landingList.getTop());

        landingCustomAdapter = new LandingCustomAdapter(
                getApplicationContext(), options, height);
        landingList.setAdapter(landingCustomAdapter);

        landingCustomAdapter.notifyDataSetChanged();

        landingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String option = options.get(i);

                if (option.contains("Receipts")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }
                else if (option.contains("Discounts")) {
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), BudgetActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
