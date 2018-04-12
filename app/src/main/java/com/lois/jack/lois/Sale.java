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
    private String type;

    public Sale(String shopName, String priceTotal, String timestamp, String type) {
        this.shopName = shopName;
        this.priceTotal = priceTotal;
        this.timestamp = timestamp;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

