package com.maxy.wutian.get;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.utils.ResFileFilter;
import com.maxy.wutian.utils.ShellUtils;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetTranslateHelper {
    public static final String WRITE_FIELNMAE_SPLIT = "    //-----------------------------";

    private String projectName;
    private String projectPath;
    private String outPutPath;
    private String lastTag;
    private String compareDir;
    public GetTranslateHelper(String projectName, String projectPath, String outPutPath, String lastTag) {
        this(projectName, projectPath, outPutPath, lastTag, "values-ar");
    }

    public GetTranslateHelper(String projectName, String projectPath, String outPutPath, String lastTag, String compareDir) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.outPutPath = outPutPath;
        this.lastTag = lastTag;
        this.compareDir = compareDir;
    }

    public void start() {
        startGetTranslate();
    }

    private void startGetTranslate() {
        //1. delete origin shareit file;
        deleteShareitFile(getDesktopShareitFile());

        File shareitPath = new File(projectPath);
        //read values file to map
        Map<String, Map<String, Map<String, String>>> valuesMap = new HashMap<>(); // module -- file -- strings.
        readStringsToMap(valuesMap, shareitPath, "values");

        //read values-XX file to map
        Map<String, Map<String, Map<String, String>>> valuesXXMap = new HashMap<>();
        readStringsToMap(valuesXXMap, shareitPath, compareDir);

        //read last tag values file to map
        Map<String, Map<String, Map<String, String>>> preValueMap = new HashMap<>();
        if (lastTag == null) {
            ShellUtils.checkoutToTag(projectPath, lastTag);
            System.out.println("has checkout to:" + lastTag);
            LogManager.getInstance().log("has checkout to:" + lastTag);
            readStringsToMap(preValueMap, shareitPath, "values");
            ShellUtils.checkoutToTag(projectPath, "master");
        }

        printTranslateFile(valuesMap, valuesXXMap, preValueMap);
    }

    private void readStringsToMap(Map<String, Map<String, Map<String, String>>> valuesMap, File file, String valueDir) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles(new ResFileFilter())) {
                readStringsToMap(valuesMap, listFile, valueDir);
            }
        } else {
            if (!TranslateFilter.isStringsFile(file))
                return;

            if (!isValuesFile(file, valueDir))
                return;
            String fileModule = getFileModule(file);
            Map<String, Map<String, String>> mapMap = valuesMap.get(fileModule);
            if (mapMap == null)
                mapMap = new HashMap<>();

            Map<String, String> fileMap = FileUtils.readStringToLinkedHasMap(file);
            mapMap.put(file.getName(), fileMap);
            valuesMap.put(fileModule, mapMap);
        }
    }

    private String getFileModule(File file) {
        String parent = file.getParent();
        String module = parent.replace(projectPath, "");
        module = module.replace("/", "_");
        if (module.contains("src"))
            return module.substring(1, module.indexOf("src") - 1);
        else
            return module.substring(1, module.indexOf("res") - 1);
    }

    private boolean isValuesFile(File file, String valueDir) {
        return valueDir.equals(file.getParentFile().getName());
    }

    private void printTranslateFile(Map<String, Map<String, Map<String, String>>> valuesMap, Map<String, Map<String, Map<String, String>>> valuesXXMap, Map<String, Map<String, Map<String, String>>> preValueMap) {
        Set<String> keySet = valuesMap.keySet();
        for (String key : keySet) {
            //key ----> module
            Map<String, Map<String, String>> valueFile = valuesMap.get(key);
            Map<String, Map<String, String>> valueXXFile = valuesXXMap == null ? null : valuesXXMap.get(key);
            Map<String, Map<String, String>> preValueFile = preValueMap == null ? null : preValueMap.get(key);
            toPrintTranslate(key, valueFile, valueXXFile, preValueFile);
        }
    }

    private void toPrintTranslate(String module, Map<String, Map<String, String>> valueMap, Map<String, Map<String, String>> valueXXMap, Map<String, Map<String, String>> preValueMap) {
        Set<String> keySet = valueMap.keySet();
        Map<String, List<String>> transValueMap = new HashMap<>();
        Map<String, List<String>> transZHValueMap = new HashMap<>();
        for (String key : keySet) {
            //key ----> fileName
            Map<String, String> valueFile = valueMap.get(key);
            Map<String, String> valueXXFile = valueXXMap == null ? null : valueXXMap.get(key);
            Map<String, String> preValueFile = preValueMap == null ? null : preValueMap.get(key);

            getTranslateData(module, key, valueFile, valueXXFile, preValueFile, transValueMap, transZHValueMap);
        }

        if (!transValueMap.isEmpty()) {
            writeTranslateToFile(module, transValueMap, transZHValueMap);
        }
    }

    private void getTranslateData(String module, String fileName, Map<String, String> valueMap, Map<String, String> valueXXMap, Map<String, String> preValueMap, Map<String, List<String>> transValueMap, Map<String, List<String>> transZHValueMap) {
        List<String> list = new ArrayList<>();
        //store keys to find zh values
        List<String> keys = new ArrayList<>();

        if (valueXXMap == null) {
            for (String key : valueMap.keySet()) {
                String value = valueMap.get(key);
                if (isNotTranslate(value))
                    continue;
                keys.add(key);
                list.add(value);
            }

            if (!list.isEmpty()) {
                transValueMap.put(fileName, list);
                transZHValueMap.put(fileName, getZhTranslateData(module, fileName, list, keys));
            }
        } else {
            for (String key : valueMap.keySet()) {
                String value = valueMap.get(key);
                if (isNotTranslate(value))
                    continue;

                if (!valueXXMap.containsKey(key)) {
                    keys.add(key);
                    list.add(value);
                    continue;
                }

                if (preValueMap == null)
                    continue;

                if (!preValueMap.containsKey(key)) {
                    keys.add(key);
                    list.add(value);
                    continue;
                }

                String preValue = preValueMap.get(key);
                if (!preValue.equals(value)) {
                    keys.add(key);
                    list.add(value);
                }
            }
            if (!list.isEmpty()) {
                transValueMap.put(fileName, list);
                transZHValueMap.put(fileName, getZhTranslateData(module, fileName, list, keys));
            }
        }
    }

    private List<String> getZhTranslateData(String module, String fileName, List<String> list, List<String> zhKeys) {
        if (list.isEmpty())
            return Collections.emptyList();

        //try write values-zh-rCN file
        String moduleRealPath = projectPath + "/" + module.replace("_", "/");
        String moduleResPath = moduleRealPath + "/src/main/res";
        File file = new File(moduleResPath);
        if (!file.exists()) {
            moduleResPath = moduleRealPath + "/res";
            file = new File(moduleResPath);
        }

        File zhDir = new File(file, "values-zh-rCN");
        if (!zhDir.exists()) {
            System.out.println("values-zh-rCN not exists :::: " + zhDir.getAbsolutePath());
            LogManager.getInstance().log("values-zh-rCN not exists :::: " + zhDir.getAbsolutePath());
            return Collections.emptyList();
        }

        File zhFile = new File(zhDir, fileName);
        if (!zhFile.exists()) {
            System.out.println("zhFile not exists :::: " + zhFile.getAbsolutePath());
            LogManager.getInstance().log("zhFile not exists :::: " + zhFile.getAbsolutePath());
            return Collections.emptyList();
        }

        List<String> zhTranslateList = new ArrayList<>();
        Map<String, String> zhMaps = FileUtils.readStringToMap(zhFile);
        for (String key : zhMaps.keySet()) {
            if (!zhKeys.contains(key))
                continue;
            zhTranslateList.add(zhMaps.get(key));
        }
        return zhTranslateList;
    }

    private void writeTranslateToFile(String module, Map<String, List<String>> transValueMap, Map<String, List<String>> transZHValueMap) {
        //write value date
        File valueFile = getModuleFile(module, false);
        writeTranslateToFile(valueFile, transValueMap);

        //write zhValue data
        File zhValueFile = getModuleFile(module, true);
        writeTranslateToFile(zhValueFile, transZHValueMap);
    }

    private void writeTranslateToFile(File file, Map<String, List<String>> translateData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<resources>\n");

            for (String fileName : translateData.keySet()) {
                bw.write(WRITE_FIELNMAE_SPLIT + fileName);
                bw.write("\n");
                bw.flush();
                List<String> list = translateData.get(fileName);
                for (String str : list) {
                    if (isNotTranslate(str))
                        continue;
                    bw.write("    " + str + "\n");
                    bw.flush();
                }

                bw.write("\n\n\n");
            }

            bw.write("</resources>");
        } catch (IOException e1) {
            System.out.println("writeTranslateToFile Error :: " + e1.toString());
            LogManager.getInstance().log("writeTranslateToFile Error :: " + e1.toString());
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

    private File getModuleFile(String module, boolean isZHFile) {
        File shareitFile = getDesktopShareitFile();
        File valueDir = null;
        if (isZHFile)
            valueDir = new File(shareitFile, "values-zh-rCN");
        else
            valueDir = new File(shareitFile, "values");
        if (!valueDir.exists())
            valueDir.mkdir();
        File file = new File(valueDir, module + "_strings.xml");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
        }
        return file;
    }

    private File getDesktopShareitFile() {
        File file = new File(outPutPath);
        File shareitFile = new File(file, projectName + "_Translate");
        if (!shareitFile.exists())
            shareitFile.mkdir();
        return shareitFile;
    }

    private void deleteShareitFile(File desktopShareitFile) {
        if (desktopShareitFile.isDirectory()) {
            for (File listFile : desktopShareitFile.listFiles()) {
                deleteShareitFile(listFile);
            }

            if (desktopShareitFile.listFiles().length == 0)
                desktopShareitFile.delete();
        } else {
            desktopShareitFile.delete();
        }
    }
}
