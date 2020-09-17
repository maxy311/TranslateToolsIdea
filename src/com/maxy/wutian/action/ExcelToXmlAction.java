package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.world.ExcelToFile;

import java.io.File;

public class ExcelToXmlAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        String excelPath = Messages.showInputDialog(project,
                "Place Input ExcelPath Path:",
                "Excel To Translate Xml:",
                Messages.getQuestionIcon());

        File excelFile = new File(excelPath);
        if (!excelFile.exists()) {
            throw new RuntimeException("Translate File not exist: " + excelFile.getAbsolutePath());
        }

        LogManager.getInstance().log("WriteExcelAction : " + project.getName() + "   " + project.getBasePath() + "      " + excelPath);
        ExcelToFile excelToFile = new ExcelToFile(project.getName(), excelPath);
        excelToFile.start();
    }
}
