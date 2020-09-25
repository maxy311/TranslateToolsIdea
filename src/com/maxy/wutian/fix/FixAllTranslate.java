package com.maxy.wutian.fix;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.utils.ResFileFilter;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.*;
import java.util.*;

public class FixAllTranslate {
    private Map<String, Map<String, String>> allData = new HashMap<>();

    public static void main(String[] args) {
        FixAllTranslate fixTranslate = new FixAllTranslate();
        String path = "/Users/maxy/Android/workspace/SHAREit";
        fixTranslate.start(path);
    }

    public void start(String path) {
        File projectFile = new File(path);
        getAllString(projectFile);
        int allCount = 0;
        for (String valueDir : allData.keySet()) {
            Map<String, String> stringStringMap = allData.get(valueDir);
            System.out.println(valueDir + "        " + stringStringMap.size());
            allCount+= stringStringMap.size();
        }
        System.out.println(allData.size() + "      " + allCount);
        fixStrings(projectFile);
    }

    private void fixStrings(File file) {
        if (file.isDirectory()) {
            String name = file.getName();
            if (!name.equals("res")) {
                for (File listFile : file.listFiles(new ResFileFilter())) {
                    fixStrings(listFile);
                }
            } else {
                doFixString(file);
            }
        }
    }

    private void doFixString(File resFile) {
        File valuesDir = new File(resFile, "values");
        if (!valuesDir.exists())
            return;

        for (File valuesFile : valuesDir.listFiles()) {
            if (!TranslateFilter.isStringsFile(valuesFile))
                continue;
            List<String> list = FileUtils.readXmlToList(valuesFile);
            if (list.isEmpty())
                continue;
            for (File valuesXXDir : resFile.listFiles()) {
                String valuesDirName = valuesXXDir.getName();
                if (!valuesDirName.startsWith("values"))
                    continue;
                Map<String, String> valueXXMapData = allData.get(valuesDirName);
                if (valueXXMapData == null || valueXXMapData.isEmpty()) {
                    System.out.println("alldata not contains : " + valuesDirName);
                    continue;
                }
                File valueXXFile = new File(valuesXXDir, valuesFile.getName());
                if (!valueXXFile.exists()) {
                    try {
                        valueXXFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                int valueCount = 0;
                boolean isValuesFile = valuesDirName.equals("values");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(valueXXFile))) {
                    for (String line : list) {
                        String[] strs = line.trim().split("\">");
                        if (!isValuesFile && strs.length >= 2) {
                            String key = strs[0];
                            String value = valueXXMapData.get(key);
                            if (value == null) {
                                System.out.println("value get from dataMap is Empty ::  " + valuesDirName + "     " + key);
                                continue;
                            }
                            valueCount++;
                            writer.write("    " + value);
                            writer.flush();
                            writer.newLine();
                        } else {
                            if (isValuesFile)
                                valueCount ++;
                            writer.write(line);
                            writer.flush();
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (valueCount <= 0)
                    valueXXFile.delete();
            }
        }
    }

    public void getAllString(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(new ResFileFilter());
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    String name = o1.getName();
                    String name1 = o2.getName();
                    return name1.compareTo(name);
                }
            });
            for (File listFile : files) {
                getAllString(listFile);
            }
        } else {
            if (!TranslateFilter.isStringsFile(file))
                return;
            readStringToMap(file);
        }
    }

    private void readStringToMap(File file) {
        File parentFile = file.getParentFile();
        String valueXXDir = parentFile.getName();
        Map<String, String> valuesMap = allData.get(valueXXDir);
        if (valuesMap == null) {
            valuesMap = new HashMap<>();
            allData.put(valueXXDir, valuesMap);
        }
        Map<String, String> stringStringMap = FileUtils.readStringToMap(file);
        Set<String> stringKeys = stringStringMap.keySet();
        for (String stringKey : stringKeys) {
            String value = stringStringMap.get(stringKey);
            if (isNotTranslate(value))
                continue;
            valuesMap.put(stringKey, value);
        }
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
}
