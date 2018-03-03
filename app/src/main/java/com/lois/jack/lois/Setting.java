package com.lois.jack.lois;

/**
 * Created by Jack on 03/03/2018.
 */

public enum Setting {

    BUDGET("Budget", "Change budget settings"),
    EMAIL("Email", "Set up an email address"),
    TREND("Trend", "Enable trend tracking");

    private final String optionName;
    private final String optionInfo;

    Setting(String optionName, String optionInfo) {
        this.optionName = optionName;
        this.optionInfo = optionInfo;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionInfo() {
        return optionInfo;
    }
}
