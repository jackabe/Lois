package com.lois.jack.lois;

/**
 * Created by Jack_Allcock on 15/06/2017.
 */

/**
 * This custom list adapter takes the array list of device objects from ConnectActivity
 * It then goes through this list and sets the layout components to the objects variables
 * */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Sale> recieptList;

    public ListCustomAdapter(Context context, ArrayList<Sale> list) {
        this.context = context;
        recieptList = list;
    }

    @Override
    public int getCount() {

        return recieptList.size();
    }

    @Override
    public Object getItem(int position) {

        return recieptList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * Here we inflate our layout with the view row xml file.
     * What the file does is set the look of each row of the list view.
     * */
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Sale sale = recieptList.get(position);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_row, null);

            /**
             * Set the layout components with the object variable and return to the activity/fragment.
             * */
            final TextView name = (TextView) convertView.findViewById(R.id.name);
            final TextView total = (TextView) convertView.findViewById(R.id.total);
            final TextView dateText = (TextView) convertView.findViewById(R.id.date);
            final ImageView shopImage = (ImageView) convertView.findViewById(R.id.shopImage);
            name.setText(sale.getShopName().toUpperCase());
            total.setText("£"+sale.getPriceTotal());

            String shopName = sale.getShopName();

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

            dateText.setText(day + " " + month );

            if (shopName.equals("Next")) {
                shopImage.setImageResource(R.drawable.next);
            }
            else if (shopName.equals("Topman")){
                shopImage.setImageResource(R.drawable.topmanp);
            }
            else {
                shopImage.setImageResource(R.drawable.boots);
            }


        }
        return convertView;
    }

}
