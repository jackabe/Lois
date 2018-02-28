package com.lois.jack.lois;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Jack on 20/02/2018.
 */

@Table
public class Sale extends SugarRecord {

    private String shopName;
    private String priceTotal;
    private String timestamp;

    public Sale(String shopName, String priceTotal, String timestamp) {
        this.shopName = shopName;
        this.priceTotal = priceTotal;
        this.timestamp = timestamp;
    }

    public Sale() {
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

