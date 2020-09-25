package com.maxy.wutian.world;

public class TestExcel {
    public static void main(String[] args) {
        textExcelToFile();
//        testFileToExcel();
    }

    private static void testFileToExcel() {
        String projectName = "SHAREit";
        String translatePath = "/Users/maxy/Desktop/SHAREit_Translate";
        XmlToExcel xmlToExcel = new XmlToExcel(projectName, translatePath);
        xmlToExcel.start();
    }

    private static void textExcelToFile() {
        String path = "/Users/maxy/Desktop/SHAREit_Translate.xls";
        ExcelToFile excelToFile = new ExcelToFile(path, null);
        excelToFile.start();
    }
}
