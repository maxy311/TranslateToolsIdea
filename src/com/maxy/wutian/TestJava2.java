package com.maxy.wutian;


import com.maxy.wutian.fileutils.Utils;
import com.maxy.wutian.fix.Test;
import com.maxy.wutian.world.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestJava {
    public static void main(String[] args) {
        TestJava testJava = new TestJava();
        testJava.start();
    }

    public void start() {
        String excelFilePath = "/Users/maxy/Desktop/aaaaaa.xls";
        excelFileToXmlFile(new File(excelFilePath));
    }

    private static void excelFileToXmlFile(File excelFile) {
        try (InputStream is = new FileInputStream(excelFile)) {
            // 创建输入流，读取Excel
            Workbook workBook = WorkbookFactory.create(is);
            Sheet sheet = workBook.getSheetAt(0);

            if (sheet == null) {
                System.out.println("get sheet is empty : " + excelFile.getAbsolutePath());
                return;
            }

            List<String> list = new ArrayList<>();
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;
                Cell cell = row.getCell(1);
                if (cell == null)
                    continue;
                String stringCellValue = cell.getStringCellValue().trim();
                int lastIndex = stringCellValue.lastIndexOf(".");
                int firstIndex = stringCellValue.indexOf(".", 0);
                int secondIndex = 0;
                if (firstIndex + 1 < stringCellValue.length())
                    secondIndex = stringCellValue.indexOf(".", firstIndex + 1);

                int i = stringCellValue.lastIndexOf("(");
                if (lastIndex > i)
                    lastIndex = i;

                int i1 = stringCellValue.lastIndexOf(" ");
                if (lastIndex > i1)
                    lastIndex = i1;
                if (secondIndex < lastIndex) {
                    String substring = stringCellValue.substring(secondIndex + 1, lastIndex);
                    if (!list.contains(substring)) {
                        list.add(substring);
                        System.out.println(substring);
                    }
                }
//                System.out.println(stringCellValue + " ---------------" + );
//                else
//                    System.out.println("+++++++++++++++++++    " + stringCellValue);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
