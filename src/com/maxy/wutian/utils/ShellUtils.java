package com.maxy.wutian.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtils {
    public static void checkoutToTag(String shellPath, String tag) {
        try {

            String command = shellPath + " checkout " + tag;
            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("checkoutToTag  ---  " + e.toString());
        }
    }
}
