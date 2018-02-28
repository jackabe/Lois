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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        }

        /**
         * Set the layout components with the object variable and return to the activity/fragment.
         * */
        final TextView name = (TextView) convertView.findViewById(R.id.name);
        final TextView total = (TextView) convertView.findViewById(R.id.total);
        final TextView date = (TextView) convertView.findViewById(R.id.date);
        name.setText(sale.getShopName().toUpperCase());
        total.setText("£"+sale.getPriceTotal());
        date.setText(sale.getTimestamp().toString());

//        for (Item item : sale.getPurchasedItems()) {
//            deviceName.append(item.getName() + ": " +item.getPrice() + " ");
//        }

        return convertView;
    }

}
