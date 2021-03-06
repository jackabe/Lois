package com.lois.jack.lois;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

/**
 * Activity for reading data from an NDEF Tag.
 *
 * @author Ralf Wondratschek
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    private ListView receiptList;
    private NfcAdapter mNfcAdapter;
    private ListCustomAdapter listCustomAdapter;
    private ArrayList<Sale> recieptListArray;
    private ArrayList<Sale> sales;
    private Sale saleToSend;
    static ArrayList<Item> itemsToPassOver;
    private ImageView backButton;

    private ListView itemList;
    private ArrayList<Item> itemArray;
    private ItemCustomAdapter listAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiptList = (ListView) findViewById(R.id.receiptList);
        backButton = findViewById(R.id.receipt_back);
        recieptListArray = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landing = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(landing);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {

        } else {

        }

        handleIntent(getIntent());

        populateListView();

        receiptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                saleToSend = sales.get(i);
                showDialog(saleToSend);
            }
        });

        receiptList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final Sale sale = sales.get(position);

                new FancyGifDialog.Builder(MainActivity.this)
                        .setTitle("Receipt Deletion")
                        .setMessage("Deleting a receipt will remove it for good!")
                        .setNegativeBtnText("Cancel")
                        .setPositiveBtnBackground("#551F8A")
                        .setPositiveBtnText("Ok")
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.giphy)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                sale.delete();
                                populateListView();
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {

                            }
                        })
                        .build();
                return true;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent landing = new Intent(getApplicationContext(), LandingActivity.class);
        startActivity(landing);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {

                String shopType = "";
                String shopName = "";
                String priceTotal = "";
                String delimiter = "%";
                String itemName = "";
                String itemPrice = "";
                String finalItemName = "";
                String finalItemPrice = "";
                int delimiterCheck = 0;
                ArrayList<Item> items = new ArrayList<>();

                for (int i = 0; i < result.length(); i++) {
                    char c = result.charAt(i);

                    if (c != delimiter.charAt(0)) {
                        if (delimiterCheck == 0) {
                            shopName += c;
                        }
                        else if (delimiterCheck == 1) {
                            priceTotal += c;
                        }
                        else if (delimiterCheck == 2) {
                            shopType += c;
                        }
                        else if (delimiterCheck > 2) {
                            // even (item name)
                            if ((delimiterCheck % 2) == 1) {
                                itemName += c;
                            }
                            // odd (item price)
                            else {
                                itemPrice += c;
                            }
                        }
                    }

                    else {
                        delimiterCheck += 1;

                        if (itemName.length() > 0) {
                            finalItemName = itemName;
                            itemName = "";
                        }
                        if (itemPrice.length() > 0) {
                            finalItemPrice = itemPrice;
                            itemPrice = "";
                        }

                        if (finalItemName.length() > 0 && finalItemPrice.length() > 0 ) {
                            Item item = new Item();
                            item.setName(finalItemName);
                            item.setPrice(finalItemPrice);
                            items.add(item);
                            finalItemName = "";
                            finalItemPrice = "";
                        }
                    }
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                Sale sale = new Sale(shopName, priceTotal, timestamp.toString(), shopType);
                SugarRecord.save(sale);

                Sale savedSale = Sale.findWithQuery(Sale.class, "Select * from Sale where timestamp = ?", sale.getTimestamp().toString()).get(0);

                for (Item item: items) {
                    item.setSaleId(savedSale.getId().toString());
                }

                itemsToPassOver = items;

                SugarRecord.saveInTx(items);

                populateListView();

                new PromptDialog(MainActivity.this)
                        .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                        .setAnimationEnable(true)
                        .setTitleText("Thanks! You spent: " + "£"+sale.getPriceTotal())
                        .setContentText("Your receipt has been saved to the list and emailed to you. Thank you for using Lois.")
                        .setPositiveListener(("Okay"), new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        }
    }
    
    public void populateListView() {
        sales = new ArrayList<>(Sale.listAll(Sale.class));

        Collections.reverse(sales);

        listCustomAdapter = new ListCustomAdapter(
                getApplicationContext(), sales);
        receiptList.setAdapter(listCustomAdapter);

        listCustomAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_transactions) {

        } else if (id == R.id.nav_security) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);

        } else if (id == R.id.nav_budget) {
            Intent budgetActivity = new Intent(getApplicationContext(), BudgetActivity.class);
            startActivity(budgetActivity);
        } else if (id == R.id.nav_trend) {

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDialog(Sale sale){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.receipt_popout);

        itemList = (ListView) dialog.findViewById(R.id.itemsList);

        itemArray = new ArrayList<>(Item.findWithQuery(Item.class, "Select * from Item where sale_id = ?", sale.getId().toString()));

        TextView shopName = dialog.findViewById(R.id.shopName);
        TextView shopTotalPrice = dialog.findViewById(R.id.shopTotal);
        ImageView shopImage = dialog.findViewById(R.id.shopImage);

        shopTotalPrice.setText("Price: ");
        shopName.append(sale.getShopName());
        shopTotalPrice.append(sale.getPriceTotal());

        listAdatper = new ItemCustomAdapter(
                getApplicationContext(), itemArray);
        itemList.setAdapter(listAdatper);

        listAdatper.notifyDataSetChanged();

        TextView doneButton = dialog.findViewById(R.id.receipt_done);
        TextView shopDate = dialog.findViewById(R.id.shopDate);

        Timestamp date = new Timestamp(System.currentTimeMillis());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(sale.getTimestamp());
            date = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) {
        }

        Integer dayInt = date.getDay();
        String day = dayInt.toString();
        int monthInt = date.getMonth();
        String month = "";

        if (day.length() == 1) {
            day = "0"+day;
        }

        if (monthInt == 1) {
            month = "January";
        } else if (monthInt == 2) {
            month = "February";
        } else if (monthInt ==3) {
            month = "March";
        } else if (monthInt == 4) {
            month = "April";
        } else if (monthInt == 5) {
            month = "May";
        } else if (monthInt == 6) {
            month = "June";
        } else if (monthInt == 7) {
            month = "July";
        } else if (monthInt == 8) {
            month = "August";
        } else if (monthInt == 9) {
            month = "September";
        } else if (monthInt == 10) {
            month = "October";
        } else if (monthInt == 11) {
            month = "November";
        } else {
            month = "December`";
        }

        if (sale.getShopName().equals("Next")) {
            shopImage.setImageResource(R.drawable.next);
        }
        else if (sale.getShopName().equals("Topman")){
            shopImage.setImageResource(R.drawable.topmanp);
        }
        else {
            shopImage.setImageResource(R.drawable.boots);
        }

        shopDate.setText(day + " " + month + " at " + date.getHours() + ":" +date.getMinutes());
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}

