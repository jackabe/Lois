package com.lois.jack.lois;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;

/**
 * Created by Jack on 21/02/2018.
 */

@Table
public class Item extends SugarRecord{

    private String name;
    private String price;
    private String saleId;

    public Item(String name, String price, String saleId) {
        this.name = name;
        this.price = price;
        this.saleId = saleId;
    }

    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }
}
