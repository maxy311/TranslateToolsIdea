package com.maxy.wutian.world;

import com.maxy.wutian.log.LogManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.util.*;

public class XmlToExcel {
    private String translatePath;
    private String excelFilePath;

    public XmlToExcel(String projectName, String translatePath) {
        this.translatePath = translatePath;
        File file = new File(translatePath);
        excelFilePath = file.getParentFile().getAbsolutePath() + File.separator + projectName + "_Translate.xls";
    }

    public void start() {
        File file = new File(translatePath);
        if (!file.exists())
            throw new RuntimeException(file.getAbsolutePath() + " not Exist!!!");

        File valueDir = new File(translatePath, "values");
        File valueZhDir = new File(translatePath, "values-zh-rCN");

        Map<String, Map<String, Map<String, List<String>>>> dataMap = new LinkedHashMap<>();
        for (File valueFile : valueDir.listFiles()) {
            if (valueFile.isHidden())
                continue;
            if (isReleasenoteFile(valueFile))
                continue;
            File valueZhFile = new File(valueZhDir, valueFile.getName());
            LogManager.getInstance().log("WriteToExcelFile:  " + valueZhFile.getAbsolutePath());
            Map<String, Map<String, List<String>>> map = getWriteExcelData(valueFile, valueZhFile);
            dataMap.put(valueFile.getName(), map);
        }
        File excelFile = new File(excelFilePath);
        if (excelFile.exists())
            excelFile.delete();
        writeToExcel(dataMap);
    }

    private void writeToExcel(Map<String, Map<String, Map<String, List<String>>>> dataMap) {
        OutputStream out = null;
        try {
            File excelFile = new File(excelFilePath);
            Workbook workbook = ExcelUtils.getWorkbook(excelFile);
            String xlsPath = excelFile.getAbsolutePath();
            if (workbook == null)
                throw new RuntimeException("Create workbook Error!   " + xlsPath);

            // sheet 对应一个工作页
            Sheet sheet = workbook.createSheet("translate");
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            System.out.println("原始数据总行数，除属性列：" + rowNumber);
            for (int i = 1; i <= rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }

            out = new FileOutputStream(xlsPath);
            workbook.write(out);

            Set<String> fileNameSet = dataMap.keySet();
            int rowIndex = 0;
            // add Header values-zh-rCN/values;
            addHeader(sheet);

            rowIndex++;
            for (String fileNameKey : fileNameSet) {
                // module
                int moduleStartRow = rowIndex;
                // module 名字
                Row row = sheet.createRow(rowIndex);
                Cell moduleNameCell = row.createCell(0);
                moduleNameCell.setCellValue(fileNameKey);

                Map<String, Map<String, List<String>>> fileNameMap = dataMap.get(fileNameKey);
                for (String fileName : fileNameMap.keySet()) {
                    int fileNameStartRow = rowIndex;
                    Cell fileNameCell = row.createCell(2);
                    CellStyle cellStyle = fileNameCell.getCellStyle();
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
                    fileNameCell.setCellStyle(cellStyle);
                    fileNameCell.setCellValue(fileName);

                    Map<String, List<String>> keyValueMap = fileNameMap.get(fileName);
                    Set<String> keyValueSet = keyValueMap.keySet();

                    for (String key : keyValueSet) {
                        int cellIndex = 4;
                        List<String> list = keyValueMap.get(key);
                        for (int i = 0; i < list.size(); i++) {
                            Cell cell = row.createCell(cellIndex + i);
                            cell.setCellValue(list.get(i));
                        }
                        rowIndex++;
                        row = sheet.createRow(rowIndex);
                    }
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(fileNameStartRow, rowIndex - 1, 2, 3);
                    sheet.addMergedRegion(cellRangeAddress);

                    rowIndex++;
                    row = sheet.createRow(rowIndex);
                }

                sheet.addMergedRegion(new CellRangeAddress(moduleStartRow, rowIndex - 2, 0, 1));
                rowIndex++;
                sheet.createRow(rowIndex);
            }
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out = new FileOutputStream(xlsPath);
            workbook.write(out);
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }

    private void addHeader(Sheet sheet) {
        Row firstRow = sheet.createRow(0);
        sheet.setColumnWidth(4, 20000);
        Cell zhCell = firstRow.createCell(4);
        zhCell.setCellValue("values-zh-rCN");
        sheet.setColumnWidth(5, 20000);
        Cell enCell = firstRow.createCell(5);
        enCell.setCellValue("values");
    }

    private static Map<String, Map<String, List<String>>> getWriteExcelData(File valueFile, File valueZhFile) {
        Map<String, Map<String, String>> valueMap = readStringToMap(valueFile);
        Map<String, Map<String, String>> valueZhMap = readStringToMap(valueZhFile);
        Map<String, Map<String, List<String>>> map = new LinkedHashMap<>();
        Set<String> keySet = valueMap.keySet();
        for (String key : keySet) {
            //key file_name;
            Map<String, List<String>> keyValueMap = new LinkedHashMap<>();
            map.put(key, keyValueMap);

            Map<String, String> keyValue = valueMap.get(key);
            Map<String, String> zhKeyValue = valueZhMap.get(key);
            for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                String entryKey = entry.getKey();
                String entryValue = entry.getValue();
                String zhEntryValue = zhKeyValue != null ? zhKeyValue.get(entryKey) : "";
                List<String> list = new ArrayList<>();
                list.add(zhEntryValue);
                list.add(entryValue);
                keyValueMap.put(entryKey, list);
            }
        }
        return map;
    }

    private static Map<String, Map<String, String>> readStringToMap(File file) {
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        if (!file.exists())
            return map;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Map<String, String> values = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.contains("<?xml version=\"1.0\" encoding=\"utf-8\"?>") || line.contains("resources>"))
                    continue;

                if (line.contains("//-----------------------------")) {
                    String fileName = line.trim().replace("//-----------------------------", "");
                    values = new LinkedHashMap<>();
                    map.put(fileName, values);
                    continue;
                }

                if (line == null || line.equals(""))
                    continue;
                if (values == null)
                    throw new RuntimeException("Create Map is Empty");

                if (line.length() == 0)
                    continue;

                String[] split = line.split("\">");
                values.put(split[0], line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    private static boolean isReleasenoteFile(File file) {
        String name = file.getName();
        return name.toLowerCase().contains("release");
    }
}
