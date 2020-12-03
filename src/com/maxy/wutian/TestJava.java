package com.maxy.wutian;

import com.maxy.wutian.utils.ShellUtils;

public class TestJava {
    public static void main(String[] args) {
//        String str = "jfkadjfksfjawwwwwwwwwfdkjafkjask";
//        String special = "wwwwwwwww";
//        int indexOf = str.indexOf(special);
//        System.out.println(str.charAt(indexOf + special.length()));

        String path = "/Users/maxy/Android/workspace/SHAREit";
        String tag = "master";
        boolean currentBranch = ShellUtils.checkoutToTag(path, tag);
        System.out.println(currentBranch);
    }
}
