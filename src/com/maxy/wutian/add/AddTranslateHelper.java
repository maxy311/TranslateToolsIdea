package com.maxy.wutian.add;

import java.io.File;

public class AddTranslateHelper {
    private File projectFile;
    private File translateFile;

    public AddTranslateHelper(File projectFile, File translateFile) {
        this.projectFile = projectFile;
        this.translateFile = translateFile;
    }

    public void startAddTranslate() {
        checkTranslateDir(translateFile);
        // 1. split file
        File splitRootDir = getSplitFileRootDir();
        SplitHelper splitHelper = new SplitHelper(projectFile, translateFile, splitRootDir);
        splitHelper.startSplit();

        AddTaskManager addTaskManager = new AddTaskManager();
        for (File file : splitRootDir.listFiles()) {
            addTranslate(addTaskManager, splitRootDir, file);
        }
        addTaskManager.tryRelease();
    }

    private void addTranslate(AddTaskManager addTaskManager, File splitRootDir, File file) {
        if (!file.isDirectory() || file.isHidden()) {
            System.out.println("addTranslate Error :: " + file.getName());
            return;
        }

        File[] childFiles = file.listFiles();
        boolean childIsValues = childFiles[0].getName().startsWith("values");

        if (!childIsValues) {
            for (File childFile : childFiles) {
                addTranslate(addTaskManager, splitRootDir, childFile);
            }
            return;
        }
        File moduleResFile = getProjectModuleResFile(splitRootDir, file);
        addTaskManager.start(file, moduleResFile);
    }

    private File getProjectModuleResFile(File splitFileRootDir, File file) {
        String filePath = file.getAbsolutePath();
        String modulePath = filePath.replace(splitFileRootDir.getAbsolutePath(), "");
        File moduleFile = new File(projectFile, modulePath);

        File resFile = new File(moduleFile, "src/main/res");
        if (!resFile.exists())
            resFile = new File(moduleFile, "res");
        return resFile;
    }

    private void checkTranslateDir(File translateDir) {
        if (!translateDir.exists())
            throw new RuntimeException(translateDir.getName() + " not exists!  " + translateDir.getAbsolutePath());

        File valueDir = new File(translateDir, "values");
        if (!valueDir.exists())
            throw new RuntimeException("value dir not exists::: " + valueDir);
    }

    private File getSplitFileRootDir() {
        File parentFile = translateFile.getParentFile();
        File splitDir = new File(parentFile, projectFile.getName());
        if (!splitDir.exists())
            splitDir.mkdir();
        else {
            delateAllSubFile(splitDir);
        }
        return splitDir;
    }

    private void delateAllSubFile(File file) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                delateAllSubFile(listFile);
            }
        } else {
            file.delete();
        }
    }
}
