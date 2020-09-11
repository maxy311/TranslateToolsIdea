package com.maxy.wutian.world;

public class TestExcel {
    public static void main(String[] args) {
        String projectName = "SHAREit";
        String translatePath = "/Users/maxy/Desktop/SHAREit_Translate";
        WriteToExcelFile writeToExcelFile = new WriteToExcelFile(projectName, translatePath);
        writeToExcelFile.start();
    }
}
