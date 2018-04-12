package com.lois.jack.lois;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private DonutProgress donutProgress;

    private SharedPreferences settingsPreferences;
    private SharedPreferences.Editor editor;
    private int budget;
    private TextView budgetText;
    private TextView budgetTitle;
    private String budgetValue;
    private String budgetTimescale;
    private Boolean isBudgetEnabled;
    private ImageView backButton;
    private ImageView settings;
    private TextView weekText;
    private TextView monthText;
    private TextView yearText;
    private ArrayList<Sale> sales;
    private TextView textStart;
    private TextView textEnd;
    private TextView shopName;
    private TextView total;
    private ImageView shopImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        settingsPreferences = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);
        donutProgress = findViewById(R.id.donut_progress);

        shopImage = findViewById(R.id.shopImage);
        shopName = findViewById(R.id.name);
        weekText = findViewById(R.id.weekText);
        monthText = findViewById(R.id.monthText);
        total = findViewById(R.id.total);
        yearText = findViewById(R.id.yearText);
        backButton = findViewById(R.id.budget_back);
        settings = findViewById(R.id.action_settings);
        budgetText = findViewById(R.id.budgetAmount);
        budgetTitle = findViewById(R.id.budgetTitle);
        textStart = findViewById(R.id.thisMonth);
        textEnd = findViewById(R.id.thisMonthOn);
        getBudget();
        budgetValue = calculateBudgetFigure();
        getBudget();

        sales = getSales();

        setLastTransaction();
        calculateMostSpent();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landing = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(landing);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder dialog = new MaterialDialog.Builder(BudgetActivity.this)
                            .titleColorRes(R.color.colorDivider)
                            .contentColor(getColor(R.color.colorDivider)) // notice no 'res' postfix for literal color
                            .dividerColorRes(R.color.colorDivider)
                            .backgroundColorRes(R.color.colorPrimary)
                            .title("Budget Settings:")
                            .positiveColor(getColor(R.color.colorDivider))
                            .negativeColor(getColor(R.color.colorDivider))
                            .neutralColor(getColor(R.color.colorDivider))
                            .positiveText("Save")
                            .neutralText("Advanced Settings")
                            .negativeText("Cancel")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                                    startActivity(settings);
                                }
                            })
                            .content("Enter your new budget amount:")
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .input(String.valueOf(budget), "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    editor = settingsPreferences.edit();
                                    editor.putInt("budgetValue", Integer.parseInt(input.toString()));
                                    editor.apply();
                                    getBudget();
                                    budgetValue = calculateBudgetFigure();
                                    if (Integer.parseInt(budgetValue) > 100) {
                                        donutProgress.setMax(Integer.parseInt(budgetValue));
                                        ProgressBarAnimation anim = new ProgressBarAnimation(donutProgress, 0, Integer.parseInt(budgetValue));
                                        anim.setDuration(1000);
                                        donutProgress.startAnimation(anim);
                                    }
                                    else {
                                        donutProgress.setMax(100);
                                        ProgressBarAnimation anim = new ProgressBarAnimation(donutProgress, 0, Integer.parseInt(budgetValue));
                                        anim.setDuration(1000);
                                        donutProgress.startAnimation(anim);
                                    }
                                }
                            });

                    dialog.show();
            }
        });


        if (isBudgetEnabled) {
            donutProgress.setTextColor(Color.WHITE);
            budgetValue = calculateBudgetFigure();

            if (Integer.parseInt(budgetValue) > 100) {
                donutProgress.setMax(Integer.parseInt(budgetValue));
                ProgressBarAnimation anim = new ProgressBarAnimation(donutProgress, 0, Integer.parseInt(budgetValue));
                anim.setDuration(1000);
                donutProgress.startAnimation(anim);
            }
            else {
                donutProgress.setMax(100);
                ProgressBarAnimation anim = new ProgressBarAnimation(donutProgress, 0, Integer.parseInt(budgetValue));
                anim.setDuration(1000);
                donutProgress.startAnimation(anim);
            }
        }

        else {
            donutProgress.setVisibility(View.GONE);
            budgetText.setVisibility(View.GONE);
            budgetTitle.setVisibility(View.GONE);

            new FancyAlertDialog.Builder(BudgetActivity.this)
                    .setTitle("You have not enabled Budget")
                    .setBackgroundColor(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                    .setMessage("Would you like to enable this in settings")
                    .setNegativeBtnText("No")
                    .setPositiveBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("Take me to settings")
                    .setNegativeBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                    .setAnimation(Animation.SLIDE)
                    .isCancellable(false)
                    .setIcon(R.mipmap.settings_w, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(settings);
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainActivity);
                        }
                    })
                    .build();
        }
    }

    private String calculateBudgetFigure() {
        List<Sale> sales = new ArrayList<>();

        if (budgetTimescale.contains("Daily")) {
            sales = Sale.findWithQuery(Sale.class, "SELECT * FROM Sale WHERE timestamp BETWEEN datetime('now', 'start of day') AND datetime('now', 'localtime')");
        }
        else if (budgetTimescale.contains("Weekly")) {
            sales = Sale.findWithQuery(Sale.class, "SELECT * FROM Sale WHERE timestamp BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime')");
            weekText.setTextColor(getColor(R.color.colorPrimary));
            monthText.setTextColor(getColor(R.color.white));
            yearText.setTextColor(getColor(R.color.white));
            weekText.setBackgroundResource(R.color.white);
            monthText.setBackgroundColor(Color.TRANSPARENT);
            yearText.setBackgroundColor(Color.TRANSPARENT);
        }
        else if (budgetTimescale.contains("Monthly")) {
            sales = Sale.findWithQuery(Sale.class, "SELECT * FROM Sale WHERE timestamp BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime')");
            weekText.setTextColor(getColor(R.color.white));
            monthText.setTextColor(getColor(R.color.colorPrimary));
            yearText.setTextColor(getColor(R.color.white));
            weekText.setBackgroundColor(Color.TRANSPARENT);
            monthText.setBackgroundResource(R.color.white);
            yearText.setBackgroundColor(Color.TRANSPARENT);
        }
        else if (budgetTimescale.contains("Yearly")) {
            sales = Sale.findWithQuery(Sale.class, "SELECT * FROM Sale WHERE timestamp BETWEEN datetime('now', 'start of year') AND datetime('now', 'localtime')");
            weekText.setTextColor(getColor(R.color.white));
            monthText.setTextColor(getColor(R.color.white));
            yearText.setBackgroundColor(Color.TRANSPARENT);
            weekText.setBackgroundColor(Color.TRANSPARENT);
            monthText.setBackgroundResource(R.color.colorPrimary);
            yearText.setBackgroundResource(R.color.white);
        }

        int storedBudget = settingsPreferences.getInt("budgetValue", 0);
        int totalSpent = 0;
        for (Sale sale: sales) {
            totalSpent += (int)Double.parseDouble(sale.getPriceTotal());
        }
        budgetText.setText("You have spent £" + totalSpent + " of your " + "£" + storedBudget + " " + budgetTimescale + " budget");

        double storedBudgetDouble = (double) storedBudget;
        double totalSpentDouble = (double) totalSpent;
        double difference = (totalSpentDouble/storedBudgetDouble)*100;
        int diffInt = (int) difference;

        return String.valueOf(diffInt);

    }

    private void getBudget() {
        budget = settingsPreferences.getInt("budgetValue", 0);
        isBudgetEnabled = settingsPreferences.getBoolean("isBudgetEnabled", false);
        budgetTimescale = settingsPreferences.getString("budgetTimescale", null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    public ArrayList<Sale> getSales() {
        return new ArrayList<>(Sale.listAll(Sale.class));
    }

    public void calculateMostSpent() {
        String date = "";

        if (budgetTimescale.contains("Weekly")) {
            date = "week";
        }
        else if (budgetTimescale.contains("Monthly")) {
            date = "month";
        }
        else if (budgetTimescale.contains("Yearly")) {
            date = "year";
        }

        String mostBought = "";
        int count = 0;

        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < sales.size(); i++) {

            if (map.containsKey(sales.get(i).getType())) {
                map.put(sales.get(i).getType(), map.get(sales.get(i).getType()) + 1 );
            }
            else
            {
                map.put(sales.get(i).getType(), 1);
            }
        }

        int maxValueInMap=(Collections.max(map.values()));  // This will return max value in the Hashmap
        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            if (entry.getValue()==maxValueInMap)
            {
                mostBought = entry.getKey();
            }
        }

        int spent = 0;

        for (Sale sale: sales) {
           if (sale.getType().equals(mostBought)) {
               spent += (int)Double.parseDouble(sale.getPriceTotal());
           }
        }

        textStart.setText("This " + date + " you have spent the most on ");
        textEnd.setText(mostBought.toUpperCase() + " - £" + spent );
    }

    public void setLastTransaction() {
        Sale sale = sales.get(0);
        String shop = sale.getShopName();
        shopName.setText(shop);
        total.setText("£"+sale.getPriceTotal());

        switch (shop) {
            case "Next":
                shopImage.setImageResource(R.drawable.next);
                break;
            case "Topman":
                shopImage.setImageResource(R.drawable.topmanp);
                break;
            default:
                shopImage.setImageResource(R.drawable.boots);
                break;
        }

    }


}
