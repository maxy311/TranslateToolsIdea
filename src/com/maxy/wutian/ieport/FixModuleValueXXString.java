package com.maxy.wutian.ieport;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class FixModuleValueXXString {
    private String PROJECT_ROOT_PATH = "/Users/maxiaoyu/Android/workspace/SHAREit";
    private String moduleValuesPath = "/Users/maxiaoyu/Android/workspace/SHAREit2/BizLocal/ModuleVideoPlayer/src/main/res/values";

    public FixModuleValueXXString(String projectRootPath, String moduleValuesPath) {
        PROJECT_ROOT_PATH = projectRootPath;
        this.moduleValuesPath = moduleValuesPath;
    }

    public void startGet() {
        List<String> specialDir = getAllValueDirName();
        System.out.println(specialDir.size() + "     " + specialDir);

        File appRootFile = new File(PROJECT_ROOT_PATH);

        File valueFilesDir = new File(moduleValuesPath);
        if (!valueFilesDir.exists())
            return;

        // 1. 将所有的value-xx 读取到map 中；
        LinkedHashMap<String, Map<String, String>> valueDir_keyValueMap = new LinkedHashMap<>();
        for (String valueDir : specialDir) {
            Map<String, String> keyValueMap = new HashMap<>();
            readAllStringToMap(valueDir, keyValueMap, appRootFile);
            valueDir_keyValueMap.put(valueDir, keyValueMap);
            System.out.println(valueDir + "     " + keyValueMap.size());
        }


        //2. 读取values 目录的文件到Map<FileName, Map<Str_Key, Str_Vale>>
        Map<String, Map<String, String>> name_Key_ValueMap = new HashMap<>();
        File[] valueXMLs = valueFilesDir.listFiles(new TranslateFilter());
        for (File valueXML : valueXMLs) {
            try {
                Map<String, String> strKeyValueMap = FileUtils.readStringToLinkedHasMap(valueXML);
                name_Key_ValueMap.put(valueXML.getName(), strKeyValueMap);
                System.out.println(valueXMLs + " -----------  " + name_Key_ValueMap);
            } catch (Exception e) {}
        }


        File outResDir = valueFilesDir.getParentFile();
        for (String valueDir : specialDir) {
            File outValueXXDir = new File(outResDir, valueDir);
            if (!outValueXXDir.exists()) {
                outValueXXDir.mkdirs();
            }

            Map<String, String> allValueXXKeyMap = valueDir_keyValueMap.get(valueDir);
            if (valueDir_keyValueMap.isEmpty())
                continue;

            Set<String> fileNameKeys = name_Key_ValueMap.keySet();
            for (String fileName : fileNameKeys) {
                File value_xx_XMLFile = new File(outValueXXDir, fileName);
                Map<String, String> valueKeyMap = name_Key_ValueMap.get(fileName);
                if (!value_xx_XMLFile.exists()) {
                    try {
                        value_xx_XMLFile.createNewFile();
                    } catch (Exception e) {}
                }
                writeValueToXMLFile(value_xx_XMLFile, valueKeyMap.keySet(), allValueXXKeyMap);
                System.out.println("------- "   + value_xx_XMLFile.getAbsolutePath());
            }
        }
    }

    private static void writeValueToXMLFile(File xmlFile, Set<String> valueKeys, Map<String, String> keyValueMap) {
        Map<String, String> outKeyValues = FileUtils.readStringToLinkedHasMap(xmlFile);
        try {
            BufferedWriter writer = null;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile)));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<resources>");
            writer.flush();
            writer.newLine();

            for (String key : valueKeys) {
                String outValue = outKeyValues.get(key);
                if (outValue == null)
                    outValue = keyValueMap.get(key);
                if (outValue != null) {
                    writer.write("    " + outValue);
                    writer.flush();
                    writer.newLine();
                }
            }

            writer.write("</resources>");
            writer.flush();
        } catch (Exception e) {
        }
    }
    private  static void readAllStringToMap(String dir, Map<String, String> keyValueMap, File dirFile) {
        for (File listFile : dirFile.listFiles()) {
            if (listFile.isHidden())
                continue;
            if (listFile.isDirectory()) {
                if (!listFile.getName().equals(dir)) {
                    readAllStringToMap(dir, keyValueMap, listFile);
                } else {
                    File[] files = listFile.listFiles(new TranslateFilter());
                    for (File strFile : files) {
                        Map<String, String> stringStringMap = FileUtils.readStringToMap(strFile);
                        if (keyValueMap == null) {
                            keyValueMap = new HashMap<>();
                        }

                        if (stringStringMap != null && !stringStringMap.isEmpty()) {
                            Set<String> keySet = stringStringMap.keySet();
                            for (String key : keySet) {
                                if (keyValueMap.containsKey(key)) {
                                    continue;
                                }
                                keyValueMap.put(key, stringStringMap.get(key));
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<String> getAllValueDirName() {
        String[] valueDirArray = {"ar", "bg", "bn", "cs", "de",
                "el", "es", "et", "fa",
                "fi", "fr", "hi", "hr", "hu",
                "in", "it", "iw", "ja", "ko",
                "lt", "lv", "ms", "pl", "pt-rBR",
                "pt-rPT", "ro", "ru", "sk", "sl",
                "sr", "th", "tl", "tr", "uk", "ur",
                "vi", "zh-rCN", "zh-rHK", "zh-rTW",
                "te", "ta", "mr", "pa", "kn", "ml", "ne-rNP"};
        List<String> valuesDirList = new ArrayList<>();
        for (String valueDir : valueDirArray) {
            valuesDirList.add("values-" + valueDir);
        }
        return valuesDirList;
    }
}
