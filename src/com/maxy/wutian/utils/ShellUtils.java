package com.maxy.wutian.utils;

import com.maxy.wutian.Constants;
import com.maxy.wutian.log.LogManager;

import java.io.*;

public class ShellUtils {

    public static boolean checkoutToTag(String projectPath, String tag) {
        File gitForAllFile = new File(projectPath + File.separator + "git-for-all.sh");
        if (!gitForAllFile.exists()) {
            return false;
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
            LogManager.getInstance().log(result);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getCurrentBranch(String projectPath) {
        File projectFile = new File(projectPath);
        File getBranchFile = new File(projectFile, "get_current_branch.sh");
        try {
            if (!getBranchFile.exists())
                getBranchFile.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(getBranchFile));
            bw.write(Constants.CURRENT_BRANCH_STRING);
            bw.flush();
            bw.close();

            String permissionCommand = "chmod 777 " + getBranchFile.getAbsolutePath();
            Runtime.getRuntime().exec(permissionCommand);

            String command = getBranchFile.getAbsolutePath() + " " + projectPath;
            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            LogManager.getInstance().log(result);
            return result;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        } finally {
            getBranchFile.delete();
        }
    }
}
