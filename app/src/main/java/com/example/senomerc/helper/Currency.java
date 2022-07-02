package com.example.senomerc.helper;

public class Currency {
    public static String toVND(int price) {
        return toVND(String.valueOf(price));
    }
    public static String toVND(String price) {
        if (price.length() <= 3) return price + "đ";
        if (price.length() <= 6) return price.substring(0, price.length() - 3) + "." + price.substring(price.length() - 3) + "đ";
        if (price.length() <= 9) return price.substring(0, price.length() - 6) + "." + price.substring(price.length() - 6, price.length() - 3) + "." + price.substring(price.length() - 3) + "đ";
        return "0đ";
    }
}
