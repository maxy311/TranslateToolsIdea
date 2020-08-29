package com.maxy.wutian.add;

import com.maxy.wutian.bean.CommentEntity;
import com.maxy.wutian.bean.ILineEntity;
import com.maxy.wutian.bean.StrEntity;
import com.maxy.wutian.get.PrintTranslateUtils;
import com.maxy.wutian.utils.FileReaderEx;
import com.maxy.wutian.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SplitHelper {
    private File projectFile;
    private File translateFile;
    private File splitRootDir;
    public SplitHelper(File projectFile, File translateFile, File splitRootDir) {
        this.projectFile = projectFile;
        this.translateFile = translateFile;
        this.splitRootDir = splitRootDir;
    }

    public void startSplit() {
        File valuesDir = new File(translateFile, "values");
        if (!valuesDir.exists()) {
            throw new RuntimeException("values path error:" + valuesDir.getAbsolutePath());
        }

        // 1 遍历values 目录
        for (File valueXMLFile : Objects.requireNonNull(valuesDir.listFiles())) {
            if (valueXMLFile.isHidden() || valueXMLFile.isDirectory())
                continue;
            String xmlFileName = valueXMLFile.getName();

            // 2 读取value 文件
            List<ILineEntity> lineEntities = null;
            if (isTransLateFile(valueXMLFile)) {
                lineEntities = FileReaderEx.readFileAllString(valueXMLFile, false);
            }

            // splitFile
            for (File valueXXDir : Objects.requireNonNull(translateFile.listFiles())) {
                if (valueXXDir.isHidden())
                    continue;
                File valueXXFile = new File(valueXXDir, xmlFileName);
                if (!valueXXFile.exists()) {
                    System.out.println(valueXXDir.getName() + " not exist file : " + valueXXFile.getName());
                    continue;
                }

                if (lineEntities == null || lineEntities.isEmpty()) {
                    // copy release note file
                    copyFileToReleaseNote(valueXXFile);
                    continue;
                }


                File splitDir = getSplitDir(valueXXFile);
                Map<String, ILineEntity> stringStrEntityMap = FileReaderEx.readFileAllStringToMap(valueXXFile, true);
                Set<String> keySet = stringStrEntityMap.keySet();
                BufferedWriter bw = null;
                try {
                    for (int i = 0; i < lineEntities.size(); i++) {
                        ILineEntity baseEntity = lineEntities.get(i);
                        String line = null;
                        if (baseEntity instanceof StrEntity) {
                            StrEntity strEntity = (StrEntity) baseEntity;
                            String stringKey = strEntity.getStringKey();
                            if (keySet.contains(stringKey))
                                line = stringStrEntityMap.get(stringKey).getLineText();
                        } else if (baseEntity instanceof CommentEntity) {
                            CommentEntity commentEntity = (CommentEntity) baseEntity;
                            String lineText = commentEntity.getLineText();
                            if (lineText.contains(PrintTranslateUtils.WRITE_FILENAME_SPLIT)) {
                                String fileName = lineText.replace(PrintTranslateUtils.WRITE_FILENAME_SPLIT, "");
                                File splitFile = createSplitFile(splitDir, fileName);
                                if (bw != null) {
                                    writerFooter(bw);
                                    bw.close();
                                }
                                bw = new BufferedWriter(new FileWriter(splitFile));
                                writerHeader(bw);
                                line = null;
                            }
                        }
                        if (bw != null && line != null) {
                            bw.write(line);
                            bw.newLine();
                            bw.flush();
                        }
                    }
                    if (bw != null)
                        writerFooter(bw);
                } catch (Exception e) {
                    System.out.println(e.toString());
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void writerHeader(BufferedWriter bw) throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n");
    }

    public void writerFooter(BufferedWriter bw) throws IOException {
        bw.write("</resources>");
    }

    private boolean isTransLateFile(File file) {
        if (file.isHidden())
            return false;

        if (file.isDirectory())
            return false;
        String fileName = file.getName();

        if (!fileName.endsWith(".xml"))
            return false;
        else if (fileName.toLowerCase().contains("releasenote"))
            return false;
        else if (fileName.toLowerCase().contains("release"))
            return false;
        return true;
    }

    private void copyFileToReleaseNote(File releaseNoteFile) {
        File releaseNoteDir = getReleaseNoteDir();
        if (!releaseNoteDir.exists())
            releaseNoteDir.mkdir();
        String valueXXName = releaseNoteFile.getParentFile().getName();
        File valueXXFile = new File(releaseNoteDir, valueXXName);
        if (!valueXXFile.exists())
            valueXXFile.mkdir();

        File file = new File(valueXXFile, releaseNoteFile.getName());
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.fileChannelCopy(releaseNoteFile, file);
    }

    private File getReleaseNoteDir() {
        File parentFile = translateFile.getParentFile();
        File releaseNote = new File(parentFile, "Release_Note");
        if (releaseNote.exists())
            releaseNote.mkdir();
        return releaseNote;
    }

    private File getSplitDir(File xmlFile) {
        String name = xmlFile.getName();
        String valueXXName = xmlFile.getParentFile().getName();
        String replaceName = name.replace("_strings.xml", "");
        replaceName = replaceName.replace("_", "/");
        File valueXXDir = new File(splitRootDir, replaceName + File.separator + valueXXName);
        if (!valueXXDir.exists())
            valueXXDir.mkdirs();
        return valueXXDir;
    }

    private File createSplitFile(File splitDir, String fileName) throws IOException {
        File file = new File(splitDir, fileName);
        if (!file.exists())
            file.createNewFile();
        return file;
    }

}
