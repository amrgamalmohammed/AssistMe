package com.assistmeapp.amrga.assistme.model;

/**
 * Created by amrga on 9/2/2016.
 */
public class Util {

    public static final double MIN = 60 * 1000.0;
    public static final double HOUR = 60 * MIN;
    public static final double DAY = 24 * HOUR;
    public static final double MONTH = 30 * DAY;
    public static final double YEAR = 365 * DAY;

    public static final StringBuilder sb = new StringBuilder();

    public static String concat(Object... objects) {
        sb.setLength(0);
        for (Object obj : objects) {
            sb.append(obj);
        }
        return sb.toString();
    }
}
