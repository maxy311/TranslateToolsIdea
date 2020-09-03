package com.maxy.wutian.log;

import java.io.*;

public class LogManager {
    private BufferedWriter bufferedWriter;
    private LogManager() {
    }


    private static class InnerHolder{
        private static LogManager sInstance = new LogManager();
    }

    public static LogManager getInstance() {
        return InnerHolder.sInstance;
    }

    public void initOutPath(String outPath) {
        try {
            File file = new File(outPath);
            if (!outPath.contains("Log.txt"))  {
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
        if (bufferedWriter == null)
            return;

        try {
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
        }
    }

    public void close(){
        try {
            bufferedWriter.close();
        } catch (Exception e) {}
    }
}
