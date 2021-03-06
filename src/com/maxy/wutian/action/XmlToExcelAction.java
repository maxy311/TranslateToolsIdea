package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.maxy.wutian.log.LogManager;
import com.maxy.wutian.world.XmlToExcel;

import java.io.File;

public class XmlToExcelAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        String translatePath = Messages.showInputDialog(project,
                "Place Input Translate Path:",
                "Write Translate To Excel:",
                Messages.getQuestionIcon());

        File translateFile = new File(translatePath);
        if (!translateFile.exists()) {
            throw new RuntimeException("Translate File not exist: " + translateFile.getAbsolutePath());
        }

        LogManager.getInstance().log("WriteExcelAction : " + project.getName() + "   " + project.getBasePath() + "      " + translatePath);
        XmlToExcel xmlToExcel = new XmlToExcel(project.getName(), translatePath);
        xmlToExcel.start();
    }
}
