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


    public static final String WRITE_FILENAME_SPLIT = "    //-----------------------------";
    public static final String WRITE_FILENAME_FLAVOR_SPLIT = "#";





    public static String GIT_FOR_ALL = "#!/bin/bash\n" +
            "\n" +
            "# 打印文件名称  git-for-all.sh\n" +
            "echo $0\n" +
            "# 打印传入参数数  fetct rebase \n" +
            "echo $*\n" +
            "\n" +
            "# must have git commands\n" +
            "if [ $# -eq 0 ] \n" +
            "then\n" +
            "\techo USAGE: $0 git-command git-command-parameters ...\n" +
            "\texit 0\n" +
            "fi\n" +
            "\n" +
            "ROOT=$(pwd)\n" +
            "PROJECT_PATH=$1\n" +
            "gitCommand=$*\n" +
            "gitCommand=${gitCommand#* }\n" +
            "gitkStr=\"gitk\"\n" +
            "\n" +
            "call_git_command()\n" +
            "{\n" +
            "\techo ----------------[ $1 ]\n" +
            "\tcd $1\n" +
            "\n" +
            "\t# gitcommand 是否包含 gitk\n" +
            "\tif [[ $gitCommand =~ $gitkStr ]]\n" +
            "\tthen\n" +
            "  \t\t$gitCommand\n" +
            "\telse\n" +
            "  \t\tgit $gitCommand\n" +
            "\tfi\n" +
            "\n" +
            "\t# git $input\n" +
            "\tcd $ROOT\n" +
            "}\n" +
            "\n" +
            "# find all git projects paths\n" +
            "PROJECTS=$(find $PROJECT_PATH -maxdepth 3 -type d -name \".git\" -a -not -path \"./.repo/*\" -print | sed 's/\\/.git//')\n" +
            "\n" +
            "# for each git project, do command\n" +
            "for P in $PROJECTS\n" +
            "do {\n" +
            "\tcall_git_command ${P:0}\n" +
            "}\n" +
            "done\n" +
            "\n" +
            "echo ##########################################\n" +
            "\n";
}
