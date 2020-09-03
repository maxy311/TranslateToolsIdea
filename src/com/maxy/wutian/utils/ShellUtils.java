package com.maxy.wutian.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ShellUtils {

    public static void checkoutToTag(String projectPath, String tag) {
        File gitForAllFile = new File(projectPath + File.separator + "git-for-all.sh");
        if (!gitForAllFile.exists()) {
            return;
        }
        try {
            String command = gitForAllFile.getAbsolutePath() + " checkout " + tag;
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
            e.printStackTrace();
        }
    }
}
