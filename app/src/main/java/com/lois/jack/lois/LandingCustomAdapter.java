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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LandingCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> optionList;
    private int height;

    public LandingCustomAdapter(Context context, ArrayList<String> list, int height) {
        this.context = context;
        optionList = list;
        this.height = height;
    }

    @Override
    public int getCount() {

        return optionList.size();
    }

    @Override
    public Object getItem(int position) {

        return optionList.get(position);
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

        String option = optionList.get(position);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.option_row, null);

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height/3);
            convertView.setLayoutParams(params);


            /**
             * Set the layout components with the object variable and return to the activity/fragment.
             * */
            final TextView name = (TextView) convertView.findViewById(R.id.optionName);
            final ImageView image = (ImageView) convertView.findViewById(R.id.optionImage);
            final RelativeLayout layout = convertView.findViewById(R.id.option_background);

            name.setText(option);

            if (option.contains("Receipts")) {

                image.setImageResource(R.mipmap.paper);
                layout.setBackgroundResource(R.color.receiptsBackground);

            }
            else if (option.contains("Discounts")) {
                image.setImageResource(R.drawable.star);
                layout.setBackgroundResource(R.color.discountBackground);
            }
            else {
                image.setImageResource(R.drawable.bar);
                layout.setBackgroundResource(R.color.budgetBackground);
            }

        }
        return convertView;
    }

}
