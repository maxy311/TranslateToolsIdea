package com.maxy.wutian.ieport;

import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.fileutils.Utils;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.utils.TranslateFilter;

import java.io.*;
import java.util.*;

public class ImportSpecialString {
    private String projectPath;
    private String specialPath;

    private Map<String, Map<String, String>> specialMap = new HashMap<>();

    public ImportSpecialString(String projectPath, String specialPath) {
        this.projectPath = projectPath;
        this.specialPath = specialPath;
    }

    public void start() {
        File file = new File(projectPath);
        if (!file.exists()) {
            log(file.getAbsolutePath() + " not exist");
            return;
        }

        File specialFile = new File(specialPath);
        if (!file.exists()) {
            log(specialFile.getAbsolutePath() + " not exist");
            return;
        }

        readSpecialToMap(specialFile);
        writeSpecialToProject(file);
    }

    private void readSpecialToMap(File specialFile) {
        for (File file : specialFile.listFiles()) {
            if (file.isHidden())
                continue;

            String name = file.getName();
            if (!name.startsWith("values-"))
                continue;


            Map<String, String> keyValueMap = FileUtils.readStringToMap(file);
            specialMap.put(name, keyValueMap);
        }
    }

    private void writeSpecialToProject(File projectFile) {
        for (File listFile : projectFile.listFiles()) {
            if (listFile.isHidden())
                continue;
            if (!listFile.isDirectory())
                continue;
            if (listFile.getName().equals("build"))
                continue;
            if (!listFile.getName().startsWith("values")) {
                writeSpecialToProject(listFile);
                continue;
            }

            if (listFile.getName().equals("values"))
                doWriteSpecialFile(listFile);
        }
    }

    private void doWriteSpecialFile(File valuesDir) {
        for (File valueFile : valuesDir.listFiles()) {
            if (!TranslateFilter.isStringsFile(valueFile))
                continue;

            try {
                String valueFileName = valueFile.getName();
                File resDir = valuesDir.getParentFile();
                List<String> valuesLines = FileUtils.readXmlToList(valueFile);

                Set<String> valueDirs = specialMap.keySet();
                for (String valueDir : valueDirs) {
                    Map<String, String> specialValueFile = specialMap.get(valueDir);
                    String valueXXName = valueDir.replace(".xml", "");
                    File valueXXDir = new File(resDir, valueXXName);
                    if (!valueXXDir.exists())
                        valueXXDir.mkdir();
                    File valuexxFile = new File(valueXXDir, valueFileName);
                    if (!valuexxFile.exists())
                        valuexxFile.createNewFile();
                    Map<String, String> valueXXFileMap = FileUtils.readStringToMap(valuexxFile);
                    boolean valuexxHasValue = !valueXXFileMap.isEmpty();

                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valuexxFile)));
                        String defaultValue;
                        for (String line : valuesLines) {
                            String[] strs = line.trim().split("\">");
                            if (strs.length >= 2) {
                                String key = strs[0];
                                defaultValue = null;
                                if (valueXXFileMap.containsKey(key)) {
                                    defaultValue = valueXXFileMap.get(key);
                                }

                                if (specialValueFile.containsKey(key)) {
                                    defaultValue = specialValueFile.get(key);
                                    valuexxHasValue = true;
                                }

                                if (defaultValue == null)
                                    continue;

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
                        Utils.close(writer);
                    }
                    if (!valuexxHasValue) {
                        valuexxFile.delete();

                        if (valueXXDir.listFiles().length == 0)
                            valueXXDir.delete();
                        break;
                    }
                }
            } catch (Exception e) {
            } finally {
            }
        }
    }

    private void log(String msg) {
        LogManager.getInstance().log("ImportSpecialString : " + msg);
    }
}
