package com.maxy.wutian.get;

import com.maxy.wutian.bean.CompareBean;
import com.maxy.wutian.bean.FileStrEntity;
import com.maxy.wutian.bean.ModuleBean;
import com.maxy.wutian.bean.StrEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class PrintTranslateUtils {
    public static final String WRITE_FILENAME_SPLIT = "    //-----------------------------";

    private File outFile;
    private CompareBean valueCompareBean;
    private CompareBean xxValueCompareBean;
    private CompareBean preValueCompareBean;

    public PrintTranslateUtils(File outFile, CompareBean valueCompareBean, CompareBean xxValueCompareBean, CompareBean preValueCompareBean) {
        this.outFile = outFile;
        this.valueCompareBean = valueCompareBean;
        this.xxValueCompareBean = xxValueCompareBean;
        this.preValueCompareBean = preValueCompareBean;
    }

    public void outputTranslateFile() {
        Map<String, ModuleBean> moduleBeanMap = valueCompareBean.getModuleBeanMap();
        if (moduleBeanMap == null || moduleBeanMap.isEmpty())
            return;

        Set<String> moduleNames = moduleBeanMap.keySet();
        for (String moduleName : moduleNames) {
            ModuleBean moduleBean = valueCompareBean.getModuleBean(moduleName);
            ModuleBean xxModuleBean = xxValueCompareBean != null ? xxValueCompareBean.getModuleBean(moduleName) : null;
            ModuleBean preModuleBean = preValueCompareBean != null ? preValueCompareBean.getModuleBean(moduleName) : null;
            outputModuleTranslateFile(moduleBean, xxModuleBean, preModuleBean);
        }
    }

    private void outputModuleTranslateFile(ModuleBean moduleBean, ModuleBean xxModuleBean, ModuleBean preModuleBean) {
        Map<String, FileStrEntity> fileStrEntityList = moduleBean.getFileStrEntityMap();
        if (fileStrEntityList == null || fileStrEntityList.isEmpty())
            return;

        Map<String, List<StrEntity>> transValueMap = new HashMap<>();
        Map<String, List<StrEntity>> transZHValueMap = new HashMap<>();

        Set<String> fileNameKeys = fileStrEntityList.keySet();
        for (String fileName : fileNameKeys) {
            FileStrEntity fileStrEntity = moduleBean.getFileStrEntity(fileName);
            FileStrEntity xxFileStrEntity = xxModuleBean != null ? xxModuleBean.getFileStrEntity(fileName) : null;
            FileStrEntity preFileStrEntity = preModuleBean != null ? preModuleBean.getFileStrEntity(fileName) : null;
            outputFileEntityFile(fileStrEntity, xxFileStrEntity, preFileStrEntity, transValueMap, transZHValueMap);
        }

        if (!transValueMap.isEmpty())
            writeTranslateToFile(moduleBean.getModuleName(), transValueMap, transZHValueMap);
    }

    private void outputFileEntityFile(FileStrEntity fileStrEntity, FileStrEntity xxFileStrEntity, FileStrEntity preFileStrEntity, Map<String, List<StrEntity>> transValueMap, Map<String, List<StrEntity>> transZHValueMap) {
        Map<String, StrEntity> strEntityMap = fileStrEntity.getStrEntityMap();
        Map<String, StrEntity> xxStrEntityMap = xxFileStrEntity != null ? xxFileStrEntity.getStrEntityMap() : null;
        Map<String, StrEntity> preStringStrEntityMap = preFileStrEntity != null ? preFileStrEntity.getStrEntityMap() : null;

        List<StrEntity> transList = new ArrayList<>();
        //store keys to find zh values
        List<String> transKeyList = new ArrayList<>();

        Set<String> keySet = strEntityMap.keySet();
        for (String key : keySet) {
            StrEntity strEntity = strEntityMap.get(key);
            if (strEntity == null || !strEntity.isNeedTranslate())
                continue;
            if (xxStrEntityMap == null || !xxStrEntityMap.containsKey(key)) {
                transKeyList.add(strEntity.getStringKey());
                transList.add(strEntity);
                continue;
            }


            if (preStringStrEntityMap == null)
                continue;

            StrEntity preStrEntity = preStringStrEntityMap.get(key);
            if (preStrEntity == null || !strEntity.getValue().equals(preStrEntity.getValue())) {
                transKeyList.add(strEntity.getStringKey());
                transList.add(strEntity);
                continue;
            }
        }

        if (!transList.isEmpty()) {
            transValueMap.put(fileStrEntity.getFileName(), transList);
            transZHValueMap.put(fileStrEntity.getFileName(), getZhTranslateData(fileStrEntity, transKeyList));
        }
    }

    private List<StrEntity> getZhTranslateData(FileStrEntity fileStrEntity, List<String> transKeyList) {
        if (transKeyList.isEmpty())
            return Collections.emptyList();

        File valueFile = fileStrEntity.getFile();
        File resDirFile = valueFile.getParentFile().getParentFile();
        File zhDir = new File(resDirFile, "values-zh-rCN");
        if (!zhDir.exists()) {
            System.out.println("values-zh-rCN not exists :::: " + zhDir.getAbsolutePath());
            return Collections.emptyList();
        }

        File zhFile = new File(zhDir, valueFile.getName());
        if (!zhFile.exists()) {
            System.out.println("zhFile not exists :::: " + zhFile.getAbsolutePath());
            return Collections.emptyList();
        }

        FileStrEntity zhFileStrEntity = new FileStrEntity(zhFile, fileStrEntity.getModuleName());
        Map<String, StrEntity> strEntityMap = zhFileStrEntity.getStrEntityMap();
        if (strEntityMap.isEmpty())
            return Collections.emptyList();
        List<StrEntity> zhStrEntityList = new ArrayList<>();
        for (String translateKey : transKeyList) {
            if (strEntityMap.containsKey(translateKey)) {
                zhStrEntityList.add(strEntityMap.get(translateKey));
            }
        }
        return zhStrEntityList;
    }

    private void writeTranslateToFile(String moduleName, Map<String, List<StrEntity>> transValueMap, Map<String, List<StrEntity>> transZHValueMap) {
        //write value date
        File valueFile = getModuleOutputFile(moduleName, false);
        writeTranslateToFile(valueFile, transValueMap);

        //write zhValue data
        File zhValueFile = getModuleOutputFile(moduleName, true);
        writeTranslateToFile(zhValueFile, transZHValueMap);
    }

    private void writeTranslateToFile(File file, Map<String, List<StrEntity>> translateData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<resources>\n");

            for (String fileName : translateData.keySet()) {
                bw.write(WRITE_FILENAME_SPLIT + fileName);
                bw.newLine();
                bw.flush();
                List<StrEntity> list = translateData.get(fileName);
                for (StrEntity strEntity : list) {
                    if (!strEntity.isNeedTranslate())
                        continue;
                    bw.write(strEntity.getLineText());
                    bw.newLine();
                    bw.flush();
                }

                bw.write("\n\n\n");
            }

            bw.write("</resources>");
        } catch (IOException e1) {
            System.out.println("writeTranslateToFile Error :: " + e1.toString());
        }
    }

    private File getModuleOutputFile(String module, boolean isZHFile) {
        File valueDir = null;
        if (isZHFile)
            valueDir = new File(outFile, "values-zh-rCN");
        else
            valueDir = new File(outFile, "values");
        if (!valueDir.exists())
            valueDir.mkdirs();
        File file = new File(valueDir, module + "_strings.xml");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
            System.out.println("getModuleOutputFile  ::  " + file.getAbsolutePath()  + "              " + e.toString());
        }
        return file;
    }
}
