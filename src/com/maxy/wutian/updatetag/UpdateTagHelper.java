package com.maxy.wutian.updatetag;

import com.maxy.wutian.bean.ILineEntity;
import com.maxy.wutian.bean.StrEntity;
import com.maxy.wutian.filter.StringFileFilter;
import com.maxy.wutian.utils.FileReaderEx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateTagHelper {
    private static final String PROJECT_PATH = "/Users/maxy/Android/workspace/SHAREit";
    private static final String FORMAT = "    <%1$s name=\"%2$s\" translatable=\"false\">%3$s</%4$s>";

    public static void main(String[] args) {
        File file = new File(PROJECT_PATH);
        UpdateTagHelper updateTagHelper = new UpdateTagHelper(file);
        updateTagHelper.start();
    }

    private static final long ONE_DAY = 24 * 60 * 60 * 1000L;
    private static final long UPDATE_INTERVAL = 1 * 30 * ONE_DAY; // 2 months
    private File projectFile;

    public UpdateTagHelper(File project) {
        projectFile = project;
    }

    public void start() {
        startUpdateTag(projectFile);
    }

    private void startUpdateTag(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (StringFileFilter.isNotCheckDir(listFile.getName()))
                    continue;
                if (listFile.getName().startsWith("values-"))
                    continue;
                startUpdateTag(listFile);
            }
        } else {
            if (!StringFileFilter.isStringsFile(file))
                return;

            long lastModified = file.lastModified();
            long interval = System.currentTimeMillis() - lastModified;
            if (interval < UPDATE_INTERVAL)
                return;
            System.out.println("-------" + file.getAbsolutePath() + "     " + (interval * 1f/ ONE_DAY) + "     " + lastModified + "    " + System.currentTimeMillis() + "    " + interval);
            checkToUpdateTag(file);
        }
    }

    private void checkToUpdateTag(File valueXMLFile) {
        File resFileDir = valueXMLFile.getParentFile().getParentFile();
        File valueXXDir = new File(resFileDir, "values-ar");
        if (!valueXXDir.exists()) {
            doUpdateTag(valueXMLFile, null);
        } else {
            File xxFile = new File(valueXXDir, valueXMLFile.getName());
            doUpdateTag(valueXMLFile, xxFile.exists() ? xxFile : null);
        }
    }

    private void doUpdateTag(File valueFile, File valueXXFile) {
        Map<String, ILineEntity> map = null;
        if (valueXXFile != null)
            map = FileReaderEx.readFileAllStringToMap(valueXXFile, true);

        List<ILineEntity> valuesList = FileReaderEx.readFileAllString(valueFile, false);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(valueFile))) {
            for (ILineEntity lineEntity : valuesList) {
                String line = lineEntity.getLineText();
                if (lineEntity.isNeedTranslate() && lineEntity instanceof StrEntity) {
                    StrEntity strEntity = (StrEntity) lineEntity;
                    boolean isNeedUpdateTag = map != null && map.containsKey(strEntity.getStringKey());
                    if (!isNeedUpdateTag) {
                        line = String.format(FORMAT, strEntity.getTagName(), strEntity.getStringKey(), strEntity.getValue(), strEntity.getTagName());
                    }
                }
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            System.out.println("-----------" + e.toString());
        }
    }
}
