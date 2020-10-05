package com.gnomon.epsos.service;

import java.io.Serializable;
import java.util.Date;

public class Demographics implements Serializable {

    private static final long serialVersionUID = -5946653173330618050L;
    private String label;
    private int length;
    private boolean mandatory;
    private String userValue;
    private String key;
    private String type;
    private Date userDateValue;
    private String friendlyName;
    private String format;

    public Demographics() {
        super();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUserDateValue() {
        return userDateValue;
    }

    public void setUserDateValue(Date userDateValue) {
        this.userDateValue = userDateValue;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
