package com.maxy.wutian;

public class Constants {
    public static String CURRENT_BRANCH_STRING= "# 返回$1指定的git项目的当前分支(branch)或标签名(tag)\n" +
            "# $1 git项目源码位置,为空获则默认为当前文件夹\n" +
            "function current_branch () {\n" +
            "    local folder=\"$(pwd)\"\n" +
            "    [ -n \"$1\" ] && folder=\"$1\"\n" +
            "    git -C \"$folder\" rev-parse --abbrev-ref HEAD | grep -v HEAD || \\\n" +
            "    git -C \"$folder\" describe --exact-match HEAD || \\\n" +
            "    git -C \"$folder\" rev-parse HEAD\n" +
            "}\n" +
            "current_branch $1";
}
