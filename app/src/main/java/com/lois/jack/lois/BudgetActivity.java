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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {

    private DonutProgress donutProgress;
    private Button changeBudgetButton;
    private SharedPreferences settingsPreferences;
    private SharedPreferences.Editor editor;
    private int budget;
    private TextView budgetText;
    private String budgetValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsPreferences = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);
        donutProgress = findViewById(R.id.donut_progress);
        changeBudgetButton = findViewById(R.id.changeBudget);
        budgetText = findViewById(R.id.budgetAmount);
        budgetValue = calculateBudgetFigure();
        getBudget();

        donutProgress.setTextColor(getColor(R.color.colorPrimary));
        donutProgress.setFinishedStrokeColor(getColor(R.color.colorPrimary));
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
        donutProgress.setTextSize(100);

        changeBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder dialog = new MaterialDialog.Builder(BudgetActivity.this)
                        .titleColorRes(R.color.colorAccent)
                        .contentColor(getColor(R.color.colorAccent)) // notice no 'res' postfix for literal color
                        .dividerColorRes(R.color.colorAccent)
                        .backgroundColorRes(R.color.colorPrimary)
                        .title("Budget Settings:")
                        .positiveColor(getColor(R.color.colorAccent))
                        .negativeColor(getColor(R.color.colorAccent))
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .content("Enter your new budget amount:")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input("", "", new MaterialDialog.InputCallback() {
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

    }

    private String calculateBudgetFigure() {
        List<Sale> sales = Sale.listAll(Sale.class);

        int storedBudget = settingsPreferences.getInt("budgetValue", 0);
        int totalSpent = 0;
        for (Sale sale: sales) {
            totalSpent += Integer.parseInt(sale.getPriceTotal().split("\\.", 2)[0]);
        }
        budgetText.setText("£" + totalSpent + " of " + "£" + storedBudget + " spent");

        double storedBudgetDouble = (double) storedBudget;
        double totalSpentDouble = (double) totalSpent;
        double difference = (totalSpentDouble/storedBudgetDouble)*100;
        int diffInt = (int) difference;
        return String.valueOf(diffInt);

    }

    private void getBudget() {
        budget = settingsPreferences.getInt("budgetValue", 0);
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


}
