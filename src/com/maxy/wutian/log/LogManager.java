package com.maxy.wutian.log;

import com.maxy.wutian.utils.FileDirUtils;

import java.io.*;

public class LogManager {
    private BufferedWriter bufferedWriter;

    private LogManager() {
        File deskTopFile = FileDirUtils.getDeskTopFile();
        System.out.println("--------------------"+"/Users/maxy/Desktop".equals(deskTopFile.getAbsolutePath()));
        boolean deskFileExist = deskTopFile.exists();
        if (!deskFileExist)
            deskTopFile = new File("/Users/maxy/Desktop");


        initOutPath(deskTopFile.getAbsolutePath());
        log("LogManager : " + "deskFileExist = " + deskFileExist + "---------" + deskTopFile.getAbsolutePath());
    }


    private static class InnerHolder {
        private static LogManager sInstance = new LogManager();
    }

    public static LogManager getInstance() {
        return InnerHolder.sInstance;
    }

    private void initOutPath(String outPath) {
        try {
            File file = new File(outPath);
            if (!outPath.contains("Log.txt")) {
                file = new File(file, "Log.txt");
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String log) {
        System.out.println(log);
        if (bufferedWriter == null)
            return;

        try {
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }
}
