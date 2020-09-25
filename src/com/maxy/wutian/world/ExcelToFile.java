package com.maxy.wutian.world;

import com.maxy.wutian.Constants;
import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.fileutils.Utils;
import com.maxy.wutian.log.LogManager;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;

public class ExcelToFile {
    private String excelPath;
    private String excelSheetName;

    public ExcelToFile(String excelPath, String excelSheetName) {
        this.excelPath = excelPath;
        this.excelSheetName = excelSheetName;

    }

    public void start() {
        File excelFile = new File(excelPath);
        if (!excelFile.exists()) {
            log("excelFile not exist :" + excelPath);
            return;
        }


        String excelFileName = excelFile.getName();
        File exportFileDir = new File(excelFile.getParentFile(), excelFileName.substring(0, excelFileName.lastIndexOf(".")));
        if (!exportFileDir.exists())
            exportFileDir.mkdirs();
        else {
            for (File file : exportFileDir.listFiles()) {
                FileUtils.deleteFile(file);
            }
        }
        excelFileToXmlFile(excelFile, exportFileDir, excelSheetName);
    }

    private void excelFileToXmlFile(File excelFile, File exportFileDir, String sheetName) {
        try (InputStream is = new FileInputStream(excelFile)) {
            // 创建输入流，读取Excel
            Workbook workBook = WorkbookFactory.create(is);
            Sheet sheet = null;
            if (sheetName == null || "".equals(sheetName)) {
                sheet = workBook.getSheetAt(0);
            } else {
                sheet = workBook.getSheet(sheetName);
            }
            if (sheet == null) {
                log("get sheet is empty : " + excelFile.getAbsolutePath());
                return;
            }

            int fileNameColumn = 2;
            int zhColumn = 4;
            int enColumn = 5;
            Row firstRow = sheet.getRow(0);
            int lastCellNum = firstRow.getLastCellNum();
            if (lastCellNum <= enColumn) {
                enColumn = lastCellNum - 1;
                zhColumn = enColumn - 1;
                fileNameColumn = zhColumn - 1;
            }
            BufferedWriter enBw = null;
            BufferedWriter zhBw = null;
            int moduleStartIndex = 1;
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;
                Cell firstCell = row.getCell(0);
                if (firstCell != null) {
                    String moduleName = firstCell.getStringCellValue();
                    if (moduleName != null && moduleName.length() > 0 && hasValueStr(row.getCell(zhColumn))) {
                        moduleStartIndex = rowIndex;
                        if (zhBw != null || enBw != null) {
                            writeFooter(zhBw, enBw);
                            Utils.close(zhBw);
                            Utils.close(enBw);
                        }
                        zhBw = createModuleWrite(exportFileDir, moduleName, true);
                        enBw = createModuleWrite(exportFileDir, moduleName, false);

                        writeXmlHeader(zhBw, enBw);
                    }
                }
                if (zhBw == null || enBw == null)
                    continue;

                String fileName = null;
                for (int fileNameIndex = fileNameColumn; fileNameIndex > 0; fileNameIndex--) {
                    Cell fileNameCell = row.getCell(fileNameIndex);
                    if (fileNameCell != null) {
                        fileName = fileNameCell.getStringCellValue();
                        break;
                    }
                }

                if (fileName != null && fileName.length() > 0)
                    writeXmlFileName(enBw, zhBw, fileName, rowIndex != moduleStartIndex);
                Cell zhCell = row.getCell(zhColumn);
                writeStringLine(zhBw, zhCell);
                Cell enCell = row.getCell(enColumn);
                writeStringLine(enBw, enCell);
            }
            if (zhBw != null)
                writeFooter(zhBw, enBw);

            Utils.close(zhBw);
            Utils.close(enBw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasValueStr(Cell zhCell) {
        if (zhCell == null)
            return false;
        String stringCellValue = zhCell.getStringCellValue();
        return stringCellValue != null && stringCellValue.length() > 0;
    }

    private void writeStringLine(BufferedWriter writer, Cell cell) throws IOException {
        if (cell == null)
            return;

        writer.write("    " + cell.getStringCellValue().trim());
        writer.newLine();
        writer.flush();
    }

    private BufferedWriter createModuleWrite(File exportDir, String moduleName, boolean isZHFile) throws IOException {
        File valuesDir = new File(exportDir, isZHFile ? "values-zh-rCN" : "values");
        if (!valuesDir.exists())
            valuesDir.mkdirs();
        File valueFile = new File(valuesDir, moduleName.trim());
        if (!valueFile.exists())
            valueFile.createNewFile();
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueFile)));
    }

    private void writeXmlFileName(BufferedWriter enBw, BufferedWriter zhBw, String fileName, boolean addSpace) throws IOException {
//        if (addSpace) {
        writeSpaceLine(enBw, 2);
        writeSpaceLine(zhBw, 2);
//        }

        enBw.write(Constants.WRITE_FILENAME_SPLIT + fileName);
        enBw.newLine();
        enBw.flush();

        zhBw.write(Constants.WRITE_FILENAME_SPLIT + fileName);
        zhBw.newLine();
        zhBw.flush();
    }

    private void writeSpaceLine(BufferedWriter bw, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            bw.newLine();
        }
    }

    private void writeXmlHeader(BufferedWriter zhBw, BufferedWriter enBw) throws IOException {
        writeXmlHeader(zhBw);
        writeXmlHeader(enBw);
    }

    private void writeXmlHeader(BufferedWriter bw) throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>");
        bw.newLine();
        bw.flush();
    }

    private void writeFooter(BufferedWriter zhBw, BufferedWriter enBw) throws IOException {
        writeSpaceLine(enBw, 2);
        writeSpaceLine(zhBw, 2);
        writeFooter(zhBw);
        writeFooter(enBw);
    }

    private void writeFooter(BufferedWriter bw) throws IOException {
        bw.write("</resources>");
        bw.flush();
    }

    private static void log(String str) {
        LogManager.getInstance().log("ExcelToFile-->   " + str);
    }
}
