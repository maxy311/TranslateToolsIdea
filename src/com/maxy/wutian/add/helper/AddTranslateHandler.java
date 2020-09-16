package com.maxy.wutian.add.helper;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTranslateHandler {
    private File projectRootDir;
    private File translateRootDir;
    private String projectRootPath;
    private String translateRootPath;
    private Map<String, List<String>> mValueListMaps = new HashMap<>();

    public AddTranslateHandler(String projectRootPath, String translateRootPath) {
        this.projectRootPath = projectRootPath;
        this.translateRootPath = translateRootPath;
        projectRootDir = new File(projectRootPath);
        translateRootDir = new File(translateRootPath);
    }

    public void start() {
        if (!projectRootDir.exists()) {
            LogManager.getInstance().log("AddTranslateAction projectRoot not exist: " + projectRootDir.getAbsolutePath());
            return;
        }

        if (!translateRootDir.exists()) {
            LogManager.getInstance().log("AddTranslateAction translateRoot not exist: " + translateRootDir.getAbsolutePath());
            return;
        }

        doAdd(translateRootDir);
    }

    private void doAdd(File translateRoot) {
        for (File listFile : translateRoot.listFiles()) {
            if (listFile.isDirectory()) {
                doAdd(listFile);
            } else {
                if (!TranslateFilter.isStringsFile(listFile))
                    continue;
                String translateFilePath = listFile.getAbsolutePath();
                String projectFilePath = translateFilePath.replace(translateRootPath, projectRootPath);
                doAddTranslate(getValuesList(listFile), listFile, new File(projectFilePath));
            }
        }
    }

    private List<String> getValuesList(File translateFile) {
        String translateFilePath = translateFile.getAbsolutePath();
        String projectValuesPath = translateFilePath.replace(translateRootPath, projectRootPath);
        File valueXXFile = translateFile.getParentFile();
        projectValuesPath = projectValuesPath.replace(valueXXFile.getName(), "values");
        if (mValueListMaps.containsKey(projectValuesPath)) {
            return mValueListMaps.get(projectValuesPath);
        }
        File projectValueFile = new File(projectValuesPath);
        List<String> list = FileUtils.readXmlToList(projectValueFile);
        if (list != null)
            mValueListMaps.put(projectValuesPath, list);
        return list;
    }

    public void doAddTranslate(List<String> valuesList, File translateFile, File projectFile) {
        try {
            boolean projectFileExist = projectFile.exists();
            if (!projectFileExist) {
                File parentFile = projectFile.getParentFile();
                if (!parentFile.exists())
                    parentFile.mkdirs();
                projectFile.createNewFile();

                List<String> stringList = FileUtils.readXmlToList(translateFile);
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(projectFile))) {
                    for (String str : stringList) {
                        str = ReplaceSpecialCharUtils.replaceSpecialChar(str, str);
                        bw.write(str);
                        bw.newLine();
                        bw.flush();
                    }
                }
                return;
            }
        } catch (Exception e) {
            LogManager.getInstance().log("doAddTranslate : " + e.toString());
        }


        if (valuesList == null || valuesList.isEmpty())
            throw new RuntimeException("can not get values file: " + translateFile.getName());
        Map<String, String> transMap = FileUtils.readStringToMap(translateFile);
        Map<String, String> projectMap = FileUtils.readStringToMap(projectFile);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(projectFile)));
            String defaultValue;
            for (String line : valuesList) {
                String[] strs = line.trim().split("\">");
                if (strs.length >= 2) {
                    String key = strs[0];
                    defaultValue = null;
                    if (transMap.containsKey(key)) {
                        defaultValue = transMap.get(key);
                    } else if (projectMap.containsKey(key)) {
                        defaultValue = projectMap.get(key);
                    }

                    if (null == defaultValue && projectMap.size() != 0) {
                        if (key.contains("translate")) {
                            key = key.replace("\" translate=\"false", "");
                            defaultValue = projectMap.get(key);
                        } else if (key.contains("translatable")) {
                            key = key.replace("\" translatable=\"false", "");
                            defaultValue = projectMap.get(key);
                        }

                        if (defaultValue == null)
                            continue;
                    }

                    //TODO check
                    defaultValue = ReplaceSpecialCharUtils.replaceSpecialChar(line, defaultValue);
                    writer.write("    " + defaultValue);
                    writer.flush();
                    writer.newLine();
                } else {
                    writer.write(line);
                    writer.flush();
                    writer.newLine();
                }
            }
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
