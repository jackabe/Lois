package com.lois.jack.lois;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Check;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.list.CustomListDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

import static eltos.simpledialogfragment.list.CustomListDialog.SINGLE_CHOICE;

public class SettingsActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {

    private static final String CHOICE_DIALOG = "dialogTagChoice";
    private static final String EMAIL_DIALOG = "emailDialog";
    private static final String DISABLE_EMAIL = "disableEmail";

    private ArrayList<Setting> settingsArray;
    private ListView settingsList;
    private SettingListAdapter settingListAdapter;

    private SharedPreferences settingsPreferences;
    private SharedPreferences.Editor editor;

    String EMAIL;
    String PASSWORD;
    String COUNTRY;
    String USERNAME;

    boolean isBudgetEnabled;
    boolean isTrendEnabled;
    boolean isEmailEnabled;
    String budgetTimescale;
    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsPreferences = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);

        getSettings();

        settingsList = findViewById(R.id.settingList);

        settingsArray = new ArrayList<>();
        settingsArray.add(Setting.BUDGET);
        settingsArray.add(Setting.EMAIL);
        settingsArray.add(Setting.TREND);

        settingListAdapter = new SettingListAdapter(getApplicationContext(), settingsArray);
        settingsList.setAdapter(settingListAdapter);
        settingListAdapter.notifyDataSetChanged();

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                Setting setting = settingsArray.get(i);

                if (setting == Setting.BUDGET && !isBudgetEnabled) {
                    new FancyAlertDialog.Builder(SettingsActivity.this)
                            .setTitle("You have not enabled Budget")
                            .setBackgroundColor(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setMessage("Would you like to enable it now?")
                            .setNegativeBtnText("No")
                            .setPositiveBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setPositiveBtnText("Yes")
                            .setNegativeBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setAnimation(Animation.SLIDE)
                            .isCancellable(true)
                            .setIcon(R.mipmap.settings_w, Icon.Visible)
                            .OnPositiveClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {
                                    editor = settingsPreferences.edit();
                                    editor.putBoolean("isBudgetEnabled", true);
                                    editor.apply();

                                    getSettings();

                                    showDirectChoice(view);


                                }
                            })
                            .OnNegativeClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .build();
                }

                else if (setting == Setting.EMAIL && !isEmailEnabled) {
                    new FancyAlertDialog.Builder(SettingsActivity.this)
                            .setTitle("You have not set up Email services")
                            .setBackgroundColor(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setMessage("Would you like to do that now?")
                            .setNegativeBtnText("No")
                            .setPositiveBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setPositiveBtnText("Yes")
                            .setNegativeBtnBackground(Color.parseColor("#551F8A"))  //Don't pass R.color.colorvalue
                            .setAnimation(Animation.SLIDE)
                            .isCancellable(true)
                            .setIcon(R.mipmap.settings_w, Icon.Visible)
                            .OnPositiveClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {

                                    getSettings();

                                    showEmailForm(view);


                                }
                            })
                            .OnNegativeClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .build();
                }


                else if (setting == Setting.BUDGET && isBudgetEnabled) {
                    showDirectChoice(view);
                }

                else
                if (setting == Setting.EMAIL && isEmailEnabled) {
                    showEmailForm(view);
                }

            }
        });
    }

    private void getSettings() {
        isBudgetEnabled = settingsPreferences.getBoolean("isBudgetEnabled", false);
        isTrendEnabled = settingsPreferences.getBoolean("isTrendEnabled", false);
        isEmailEnabled = settingsPreferences.getBoolean("isEmailEnabled", false);
        budgetTimescale = settingsPreferences.getString("budgetTimescale", null);
        emailAddress = settingsPreferences.getString("emailAddress", null);

    }

    public void showDirectChoice(View view){

        SimpleListDialog.build()
                .title("Change your budget settings")
                .items(new String[]{"Weekly", "Monthly", "Yearly", "Turn budget off"})
                .choiceMode(SimpleListDialog.SINGLE_CHOICE)
                .show(SettingsActivity.this, CHOICE_DIALOG);

        /** Results: {@link SettingsActivity#onResult} **/

    }

    public void showEmailForm(View view){

        String hint = "Enter email here";
        if (emailAddress != null) {
            hint = emailAddress;
        }

        SimpleFormDialog.build()
                .title("Email Setup")
                .msg("Please enter the email address that you want to connect to the app")
                .fields(
                        Input.email(EMAIL).text(hint).required(),
                        Check.box(DISABLE_EMAIL).label("Disable email services").check(false)
                )
                .show(this, EMAIL_DIALOG);

        /** Results: {@link SettingsActivity#onResult} **/

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

    /**
     * Let the hosting fragment or activity implement this interface
     * to receive results from the dialog
     *
     * @param dialogTag the tag passed to {@link SimpleDialog#show}
     * @param which result type, one of {@link #BUTTON_POSITIVE}, {@link #BUTTON_NEGATIVE},
     *              {@link #BUTTON_NEUTRAL} or {@link #CANCELED}
     * @param extras the extras passed to {@link SimpleDialog#extra(Bundle)}
     * @return true if the result was handled, false otherwise
     */
    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {

                case CHOICE_DIALOG: /** {@link SettingsActivity#showDirectChoice}, {@link SettingsActivity#showMultiChoice} **/
                    ArrayList<String> labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);
                    budgetTimescale = android.text.TextUtils.join(", ", labels);

                    if (budgetTimescale.contains("Turn budget off")) {
                        editor = settingsPreferences.edit();
                        editor.putBoolean("isBudgetEnabled", false);
                        editor.apply();
                    }
                    else {
                        editor = settingsPreferences.edit();
                        editor.putString("budgetTimescale", budgetTimescale);
                        editor.apply();
                    }

                    getSettings();
                    return true;

                case EMAIL_DIALOG: /** {@link SettingsActivity#showForm(View)} **/
                    String email = extras.getString(EMAIL);
                    boolean checkbox = extras.getBoolean(DISABLE_EMAIL);
                    editor = settingsPreferences.edit();
                    editor.putString("emailAddress", email);

                    if (!checkbox) {
                        editor.putBoolean("isEmailEnabled", true);
                    }
                    else {
                        editor.putBoolean("isEmailEnabled", false);
                    }

                    editor.commit();
                    getSettings();

                    return true;
            }

        }

        return false;

    }
}
