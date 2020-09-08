package com.maxy.wutian.add.helper;

import com.maxy.wutian.log.LogManager;

public class ReplaceSpecialCharUtils {

    public static String replaceSpecialChar(String originLine, String line) {
        checkError(originLine, line);

        line = checkAndReplaceSpecialChar(line, "\u2028", " ","2028"); //行分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\u2029", " ", "2029"); //段落分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\uFEFF", " ", "FEFF");//字节顺序标记	空白
        line = checkAndReplaceSpecialChar(line, "\u00A0", " ", "00A0");
//                line = checkAndReplaceSpecialChar(line, "%", "%");
        line = checkAndReplaceSpecialChar2(line); // '-------> \'

        try {
            if (!originLine.equals(line))
                line = replaceSpaceChar(originLine, line);
        } catch (Exception e) {
            LogManager.getInstance().log(e.toString());
        }
        return line;
    }

    private static String replaceSpaceChar(String originLine, String line) {
        String startChar = "\">";
        String space = startChar;
        while (originLine.contains(space)) {
            space += " ";
        }
        if (!space.equals(startChar)) {
            String lineSpace = startChar;
            while (line.indexOf(lineSpace) != -1) {
                lineSpace += " ";
            }

            if (!lineSpace.equals(startChar)) {
                lineSpace = lineSpace.substring(0, lineSpace.length() - 1);
                space = space.substring(0, space.length() - 1);
                line = line.replace(lineSpace, space);
            }
        }

        startChar = "</string>";
        space = startChar;
        while (originLine.contains(space)) {
            space = " " + space;
        }
        if (!space.equals(startChar)) {
            String lineSpace = startChar;
            while (line.indexOf(lineSpace) != -1) {
                lineSpace = " " + lineSpace;
            }

            if (!lineSpace.equals(startChar)) {
                lineSpace = lineSpace.substring(1, lineSpace.length());
                space = space.substring(1, space.length());
                if (!lineSpace.equals(space)) {
                   line =  line.replace(lineSpace, space);
                }
            }
        }
        return line;
    }

    public static String replaceSpecialChar(String line) {
        line = checkAndReplaceSpecialChar(line, "\u2028", " ","2028"); //行分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\u2029", " ", "2029"); //段落分隔符	行结束符
        line = checkAndReplaceSpecialChar(line, "\uFEFF", " ", "FEFF");//字节顺序标记	空白
        line = checkAndReplaceSpecialChar(line, "\u00A0", " ", "00A0");
//                line = checkAndReplaceSpecialChar(line, "%", "%");
        line = checkAndReplaceSpecialChar2(line); // '-------> \'

        return line;
    }

    private static  String checkAndReplaceSpecialChar(String line, String replaceTarget, String replaceStr, String tag) {
        boolean hasChanged = false;
        String originLine = line;
        while (line.indexOf(replaceTarget) != -1) {
            line = line.replace(replaceTarget, replaceStr);
            hasChanged = true;
        }
        if (hasChanged) {
            LogManager.getInstance().log("old    " + originLine);
            LogManager.getInstance().log("new    " + line + "        " + tag);
        }

        return line;
    }

    // '  ------------------>   \'
    private static String checkAndReplaceSpecialChar2(String line) {
        boolean hasChanged = false;
        if (line.indexOf("'") != -1) {
            char[] chars = line.toCharArray();
            char lastChar = '1';
            StringBuffer sb = new StringBuffer();
            for (char aChar : chars) {
                if ('\'' == aChar) {
                    if ('\\' != lastChar) {
                        sb.append("\\");
                        hasChanged = true;
                    }
                }
                sb.append(aChar);
                lastChar = aChar;
            }
            if (hasChanged) {
                line = sb.toString();
                sb.setLength(0);
            }
        }
        return line;
    }

    private static void checkError(String originLine, String line) {
        try {
            if (!isSpecialCountEquals("%d", originLine, line))
                LogManager.getInstance().log("checkError %d::: " + originLine + "\n" + line);

            if (!isSpecialCountEquals("%s", originLine, line))
                LogManager.getInstance().log("checkError %s::: " + originLine + "\n" + line);

            for (int i = 1; i <= 5; i++) {
                String special = "%" + i + "$s";
                if (!isSpecialCountEquals(special, originLine, line))
                LogManager.getInstance().log("checkError "+special+" ::: " + originLine + "\n" + line);
            }
        } catch (Exception e) {
            LogManager.getInstance().log("checkError  " + originLine + "\n" + line);
        }
    }

    private static boolean isSpecialCountEquals(String special, String originLine, String line) {
        return specialStrCount(special, originLine) == specialStrCount(special, line);
    }

    private static int specialStrCount(String special, String line) {
        int count = 0;
        String tempLine = line;
        while(tempLine.indexOf(special)>=0) {
            tempLine = tempLine.substring(tempLine.indexOf(special) + special.length());
            count++;
        }
        return count;
    }
}
