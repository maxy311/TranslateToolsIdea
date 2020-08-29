package com.maxy.wutian.utils;

import com.maxy.wutian.bean.CommentEntity;
import com.maxy.wutian.bean.ILineEntity;
import com.maxy.wutian.bean.StrEntity;

import java.io.*;
import java.util.*;

public class FileReaderEx {
    public static List<ILineEntity> readFileAllString(File file, boolean onlyStringTag) {
        if (file.isDirectory() || file.isHidden())
            return Collections.emptyList();

        List<ILineEntity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String trimLine = line.trim();
                if (!(trimLine.startsWith("<string") || trimLine.contains("plurals") || trimLine.contains("<item") || trimLine.contains("-array"))) {
                    if (onlyStringTag)
                        continue;
                    else
                        list.add(new CommentEntity(line));
                } else {
                    if (line.endsWith("</plurals>") || line.endsWith("</string>") || line.endsWith("</string-array>")) {
                        if (sb.length() != 0)
                            sb.append("\n" + "    " + line);
                        StrEntity strEntity = new StrEntity(sb.length() != 0 ? sb.toString() : line);
                        list.add(strEntity);
                        sb.setLength(0);
                    } else if (trimLine.startsWith("<plurals") || trimLine.startsWith("<string-array")) {
                        sb.setLength(0);
                        sb.append(line);
                    } else {
                        if (sb.length() != 0)
                            sb.append("\n" + "    " + line);
                    }
                }
            }
            return list;
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException " + e.toString());
        } catch (IOException e) {
            System.out.println("IOException" + e.toString());
        }
        return Collections.emptyList();
    }

    public static Map<String, ILineEntity> readFileAllStringToMap(File file, boolean onlyStringTag) {
        if (file.isDirectory() || file.isHidden())
            return Collections.emptyMap();

        Map<String, ILineEntity> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String trimLine = line.trim();
                if (!(trimLine.startsWith("<string") || trimLine.contains("plurals") || trimLine.contains("<item") || trimLine.contains("-array"))) {
                    if (onlyStringTag)
                        continue;
                    else
                        map.put(line, new CommentEntity(line));
                } else {
                    if (line.endsWith("</plurals>") || line.endsWith("</string>") || line.endsWith("</string-array>")) {
                        if (sb.length() != 0)
                            sb.append("\n" + "    " + line);
                        StrEntity strEntity = new StrEntity(sb.length() != 0 ? sb.toString() : line);
                        map.put(strEntity.getStringKey(), strEntity);
                        sb.setLength(0);
                    } else if (trimLine.startsWith("<plurals") || trimLine.startsWith("<string-array")) {
                        sb.setLength(0);
                        sb.append(line);
                    } else {
                        if (sb.length() != 0)
                            sb.append("\n" + "    " + line);
                    }
                }
            }
            return map;
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException " + e.toString());
        } catch (IOException e) {
            System.out.println("IOException" + e.toString());
        }
        return Collections.emptyMap();
    }
}
