package com.maxy.wutian;

public class TestJava {
    public static void main(String[] args) {
        String str = "jfkadjfksfjawwwwwwwwwfdkjafkjask";
        String special = "wwwwwwwww";
        int indexOf = str.indexOf(special);
        System.out.println(str.charAt(indexOf + special.length()));
    }
}
