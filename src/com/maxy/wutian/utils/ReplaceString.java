package com.maxy.wutian.utils;

import com.maxy.wutian.add.helper.ReplaceSpecialCharUtils;
import com.maxy.wutian.add.task.Task;
import com.maxy.wutian.fileutils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ReplaceString {
    public static void main(String[] args) {
        ReplaceString replaceString = new ReplaceString();
        replaceString.start();
    }

    private void start() {
        String path = "/Users/maxy/Android/workspace/SHAREit/BizLocal";
        List<File> strFileList = new ArrayList<>();
        getAllStringPath(new File(path), strFileList);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 1;
                int allCount = strFileList.size();
                for (File strFile : strFileList) {
                    if (strFile.getName().equals("share_strings.xml"))
                    replaceString(strFile);
//                    System.out.println(index + ":" + allCount + "       " + strFile.getAbsolutePath());
                    index ++;
                }
            }
        }).start();


    }

    private void getAllStringPath(File dir, List<File> strList) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isHidden())
                continue;
            if (file.isDirectory()) {
                getAllStringPath(file, strList);
                continue;
            }


            File parentFile = file.getParentFile();
            if (!parentFile.getName().startsWith("values"))
                continue;
            String fileName = file.getName();
            if (fileName.contains("strings.xml") || fileName.contains("string.xml"))
                strList.add(file);
        }
    }

    private void replaceString(File strFile) {
        List<String> stringList = FileUtils.readXmlToList(strFile);
        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(strFile))) {
                for (String str : stringList) {
                    str = ReplaceSpecialCharUtils.replaceSpecialChar(str, str);
                    str = doReplaceStr(str);
                    bw.write(str);
                    bw.newLine();
                    bw.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("error  "+ strFile.getAbsolutePath() + " \n    " + e.toString());
        }
    }

    private String doReplaceStr(String line) {
        if (line.contains(STR_ORI)) {
            if (line.contains(STR_REPLACE)) {
                int originIndex = line.indexOf(STR_ORI);
                int replaceIndex = line.indexOf(STR_REPLACE);
                if (originIndex == replaceIndex)
                    return line;
            }

            line = line.replaceAll(STR_ORI, STR_REPLACE);
            if (line.contains(STR_REPLACE_Fix))
                line = line.replaceAll(STR_REPLACE_Fix, STR_REPLACE);
        }
        return line;
    }

    private static final String STR_ORI = "SHAREit";
    private static final String STR_REPLACE = "SHAREit Lite";
    private static final String STR_REPLACE_Fix = "SHAREit Lite Lite";
}
