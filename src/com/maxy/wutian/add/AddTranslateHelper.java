package com.maxy.wutian.add;

import com.maxy.wutian.Constants;
import com.maxy.wutian.add.helper.AddTranslateHandler;
import com.maxy.wutian.fileutils.FileUtils;
import com.maxy.wutian.log.LogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddTranslateHelper {
    private String projectPath;
    private String translatePath;
    private String translateParentPath;
    private String translateSplitPath;

    public AddTranslateHelper(String projectName, String projectPath, String translatePath) {
        this.projectPath = projectPath;
        this.translatePath = translatePath;
        File translateFile = new File(translatePath);
        File translateParentFile = translateFile.getParentFile();
        this.translateParentPath = translateParentFile.getPath();
        translateSplitPath = translateParentPath + File.separator + projectName;
        LogManager.getInstance().log(projectPath + "    " + translatePath + "      " + translateSplitPath);
    }

    public void start() {
        File translateFile = getTranslateDir();
        FileUtils.deleteFile(translateFile);

        File file = new File(translatePath);
        checkTranslateDir(file);
        //0. add file name tag
        addFileNameTag(file);

        // 1. split file
        slipFile(file);

        // 2. add Translate
        AddTranslateHandler addTranslateHandler = new AddTranslateHandler(projectPath, translateFile.getAbsolutePath());
        addTranslateHandler.start();
    }

    private void addFileNameTag(File file) {
        Map<String, List<String>> valuesMap = readValuesToMap(file);
        for (File listFile : file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isHidden())
                    return false;
                if (file.getName().equals("values"))
                    return false;
                return true;
            }
        })) {
            for (File valueFile : listFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isHidden())
                        return false;
                    String fileName = file.getName();
                    if (!fileName.endsWith("xml"))
                        return false;
                    if (fileName.toLowerCase().contains("releasenote"))
                        return false;
                    if (fileName.toLowerCase().contains("release"))
                        return false;
                    return true;
                }
            })) {
                List<String> stringList = valuesMap.get(valueFile.getName());
                Map<String, String> stringMap = FileUtils.readStringToMap(valueFile);
                Set<String> keySet = stringMap.keySet();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(valueFile))) {
                    if (stringList == null) {
                        System.out.println(valueFile.getAbsolutePath());
                        break;
                    }
                    for (String str : stringList) {
                        String key = str.trim().split("\">")[0];
                        key = key.replaceAll(" ", " ");
                        if (keySet.contains(key)) {
                            str = "    " + stringMap.get(key);
                        }
                        bw.write(str);
                        bw.newLine();
                        bw.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Map<String, List<String>> readValuesToMap(File file) {
        File valuesDir = new File(file, "values");
        if (!valuesDir.exists()) {
            throw new RuntimeException("values path error:" + valuesDir.getAbsolutePath());
        }
        Map<String, List<String>> valuesMap = new HashMap<>();
        for (File listFile : valuesDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isHidden())
                    return false;
                String fileName = file.getName();
                if (!fileName.endsWith("xml"))
                    return false;
                if (fileName.toLowerCase().contains("releasenote"))
                    return false;
                if (fileName.toLowerCase().contains("release"))
                    return false;
                return true;
            }
        })) {
            valuesMap.put(listFile.getName(), FileUtils.readXmlToList(listFile));
        }
        return valuesMap;
    }

    private void slipFile(File file) {
        for (File valueDir : file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().startsWith("values"))
                    return true;
                return false;
            }
        })) {
            for (File xmlFile : valueDir.listFiles()) {
                if (isTransLateFile(xmlFile)) {
                    splitFileToValueXX(xmlFile);
                } else {
                    copyToReleaseNote(xmlFile);
                }
            }
        }
    }

    private void splitFileToValueXX(File xmlFile) {
        File translateDir = getTranslateDir();
        String name = xmlFile.getName();
        String replaceName = name.replace(".xml", "").replace("_", File.separator);
        File translateSrcDir = new File(translateDir, replaceName);   //src dir;
        if (!translateSrcDir.exists())
            translateSrcDir.mkdirs();

//        // out xmlFile to real file;
        splitXmlFile(translateSrcDir, xmlFile);
    }

    private void splitXmlFile(File translateSrcDir, File xmlFile) {
        BufferedWriter bw = null;
        String valueXXName = xmlFile.getParentFile().getName();
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            StringBuffer sb = new StringBuffer();
            String line;
            String str = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("<resources>") || line.contains("</resources>") || line.contains("<?xml"))
                    continue;
                if (line.contains(Constants.WRITE_FILENAME_SPLIT)) {
                    if (bw != null) {
                        writeLine(bw, "</resources>");
                        bw.close();
                    }

                    String fileName = line.replace(Constants.WRITE_FILENAME_SPLIT, "").trim();
                    bw = resetOutFileWriter(translateSrcDir, fileName, valueXXName);
                    bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<resources>\n");
                } else {
                    if (bw == null) {
                        System.out.println("ERROR ::: " + line);
                        continue;
                    }

                    line = line.trim();
                    if (!(line.startsWith("<string") || line.contains("plurals") || line.contains("<item") || line.contains("-array")))
                        continue;
                    if (line.contains("plurals") || line.contains("<item quantity")) {
                        if (line.contains("plurals")) {
                            if (line.startsWith("<plurals")) {
                                str = line.split("\">")[0];
                                sb.append(line);
                            } else if (line.startsWith("</plurals>")) {
                                sb.append("\n" + "    " + line);
                                writeLine(bw, "    " + sb.toString());
                                str = "";
                                sb.setLength(0);
                            }
                        } else if (line.contains("<item quantity")) {
                            sb.append("\n" + "        " + line);
                        }
                    } else if ((line.contains("-array") || line.contains("-array")) || (line.startsWith("<item>") && line.endsWith("</item>"))) {
                        if ((line.contains("<string-array") || line.contains("<integer-array"))) {
                            str = line;
                            sb.append(line);
                        } else if (line.startsWith("<item>") && line.endsWith("</item>")) {
                            if (str == "" || str.equals(""))
                                continue;
                            sb.append("\n" + "        " + line);
                        } else if (line.startsWith("</string-array>") || line.startsWith("</integer-array>")) {
                            sb.append("\n" + "    " + line);
                            writeLine(bw, "    " + sb.toString());
                            str = "";
                            sb.setLength(0);
                        }
                    } else {
                        writeLine(bw, "    " + line);
                    }
                }
            }
            if (bw != null)
                writeLine(bw, "</resources>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void writeLine(BufferedWriter bw, String line) throws IOException {
        bw.write(line + "\n");
        bw.flush();
    }

    private BufferedWriter resetOutFileWriter(File srcDir, String fileName, String valuesDirName) {
        BufferedWriter bw = null;
        try {
            File valueFile = new File(srcDir, fileName);
            String filePath = valueFile.getAbsolutePath();
            String valueXXFilePath = filePath.replace("values", valuesDirName);
            File valueXXFile = new File(valueXXFilePath);
            File valuesXXDir = valueXXFile.getParentFile();
            if (!valuesXXDir.exists() )
                valuesXXDir.mkdirs();

            if (!valueXXFile.exists())
                valueXXFile.createNewFile();
            bw = new BufferedWriter(new FileWriter(valueXXFile));
        } catch (IOException e) {
        }
        return bw;
    }

    private void checkTranslateDir(File translateDir) {
        if (!translateDir.exists())
            throw new RuntimeException(translateDir.getAbsolutePath() + " not exists!");

        File valueDir = new File(translateDir, "values");
        if (!valueDir.exists())
            throw new RuntimeException("value dir not exists::: " + valueDir);
    }

    private boolean isTransLateFile(File file) {
        if (file.isHidden())
            return false;

        if (file.isDirectory())
            return false;
        String fileName = file.getName();

        if (fileName.toLowerCase().contains("releasenote"))
            return false;
        if (fileName.toLowerCase().contains("release"))
            return false;

        if (!fileName.endsWith(".xml")) {
            return false;
        }
        return true;
    }

    private void copyToReleaseNote(File releaseNoteFile) {
        File releaseNoteDir = new File(translateParentPath + File.separator + "ReleaseNote");
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

    public File getTranslateDir() {
        File translateSplitPath = new File(this.translateSplitPath);
        if (translateSplitPath.exists())
            translateSplitPath.mkdir();
        return translateSplitPath;
    }

}
