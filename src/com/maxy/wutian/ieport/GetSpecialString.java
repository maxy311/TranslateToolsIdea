package com.maxy.wutian.ieport;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.utils.FileDirUtils;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.*;
import java.util.*;


/**
 * values-th
 * values-ms
 * values-in
 * values-vi
 *
 * <string name="setting_name">Settings</string>
 * <string name="setting_sz_message_notification_new">Notification Settings</string>
 * <string name="setting_notification_cmd_recommend">Receive notification</string>\
 * <string name="setting_terms_service">Terms of Service</string>
 * <string name="setting_privacy_policy">Privacy Policy</string>
 * <string name="setting_copy_right">CopyRight Policy</string>
 * <string name="setting_version_check">Check for New Version</string>
 * <string name="setting_clean_caches">Clean Caches</string>
 * <string name="setting_cache_counting">calculating...</string>
 * <string name="main_home_back_to_quit_tip">Press once again to exit</string>
 * <string name="follow_toast_failed_net">Failed to follow, please check your network connection</string>
 *
 *
 *
 * */

public class GetSpecialString {
    public static void main(String[] args) {
        GetSpecialString getSpecialString = new GetSpecialString();
        getSpecialString.start();
    }

    private String projectName;
    private File shareitFile;
    private List<String> keys;
    private List<String> value_xxList;
    private Map<String, List<String>> outMapList = new HashMap<>();

    public GetSpecialString() {
        shareitFile = new File("/Users/maxy/Android/workspace/SHAREit");
        value_xxList = new ArrayList<>();
        value_xxList.add("values-th");
        value_xxList.add("values-ms");
        value_xxList.add("values-in");
        value_xxList.add("values-vi");

        keys = new ArrayList<>();
        keys.add("<string name=\"setting_name\">");
        keys.add("<string name=\"setting_sz_message_notification_new\">");
        keys.add("<string name=\"setting_notification_cmd_recommend\">");
        keys.add("<string name=\"setting_terms_service\">");
        keys.add("<string name=\"setting_privacy_policy\">");
        keys.add("<string name=\"setting_copy_right\">");
        keys.add("<string name=\"setting_version_check\">");
        keys.add("<string name=\"setting_clean_caches\">");
        keys.add("<string name=\"setting_cache_counting\">");
        keys.add("<string name=\"main_home_back_to_quit_tip\">");
        keys.add("<string name=\"follow_toast_failed_net\">");
    }

    public GetSpecialString(String projectName, String shareitPath, String specialKeyPath) {
        this.projectName = projectName;
        shareitFile = new File(shareitPath);
        parseSpecialKey(specialKeyPath);
    }

    private void parseSpecialKey(String specialKeyPath) {
        keys = new ArrayList<>();
        value_xxList = new ArrayList<>();
        File specialKeyFile = new File(specialKeyPath);
        List<String> list = FileUtils.readXmlToList(specialKeyFile);
        for (String line : list) {
            line = line.trim();
            if (isNotTranslate(line))
                continue;
            if (line.startsWith("values-")) {
                value_xxList.add(line);
                System.out.println(line);
            }
            else {
                if (line.startsWith("<string") ||  line.startsWith("<string-array")|| line.contains("plurals") || line.contains("<item") || line.contains("-array")) {
                    String key = line.split("\">")[0].trim();
                    keys.add(key);
                    System.out.println(key);
                }
            }
        }
        LogManager.getInstance().log(keys.toString());
        LogManager.getInstance().log(value_xxList.toString());
    }

    private boolean isNotTranslate(String str) {
        if (str.contains("translate") || str.contains("translatable") || str.contains("translatable"))
            return true;
        if (str.contains("\">@string/"))
            return true;
        if (str.contains("<string name=\"app_name\">"))
            return true;
        return false;
    }

    public void start() {
        try {
            if (keys.isEmpty() || value_xxList.isEmpty())
                return;
            getStringFromFile(shareitFile);
            outToDeskFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStringFromFile(File file) {
        for (File listFile : file.listFiles()) {
            if (listFile.isHidden())
                continue;
            if (!listFile.isDirectory())
                continue;
            if (listFile.getName().equals("build"))
                continue;
            if (!listFile.getName().startsWith("values")) {
                getStringFromFile(listFile);
                continue;
            }
            String fileName = listFile.getName();
            if (value_xxList.contains(fileName)) {
                tryOutPrintString(listFile);
            }
        }
    }

    public void tryOutPrintString(File file) {
        for (File listFile : file.listFiles()) {
            if (!TranslateFilter.isStringsFile(listFile))
                continue;
            Map<String, String> fileStrMap = FileUtils.readStringToMap(listFile);
            if (fileStrMap == null || fileStrMap.isEmpty())
                continue;
            Set<String> fileKeys = fileStrMap.keySet();
            for (String key : keys) {
                if (fileKeys.contains(key)) {
                    String value = fileStrMap.get(key);
                    storeToList(listFile, value);
                }
            }
        }
    }

    private void storeToList(File file, String specialLine) {
        List<String> storeList = getStoreList(file);
        if (!storeList.contains(specialLine))
            storeList.add(specialLine);
    }

    public List<String> getStoreList(File file) {
        String mapKey = file.getParentFile().getName();
        if (outMapList.containsKey(mapKey))
            return outMapList.get(mapKey);
        List<String> list = new ArrayList<>();
        outMapList.put(mapKey, list);
        return list;
    }

    private void outToDeskFile() throws IOException {
        if (outMapList.isEmpty())
            return;
        Set<String> keySet = outMapList.keySet();
        for (String key : keySet) {
            List<String> list = outMapList.get(key);
            outSpecialListToMap(key, list);
        }
    }

    private void outSpecialListToMap(String name, List<String> list) throws IOException {
        if (list == null)
            return;
        File outputFile = getOutputFile(name);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {
            for (String str : list) {
                bufferedWriter.write(str);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
    }

    private File getOutputFile(String fileName) throws IOException {
        File deskTopFile = FileDirUtils.getDeskTopFile();
        File specialString = new File(deskTopFile, projectName + "_Special_String");
        if (!specialString.exists())
            specialString.mkdir();
        File outFile = new File(specialString, fileName + ".xml");
        if (!outFile.exists())
            outFile.createNewFile();
        return outFile;
    }
}
