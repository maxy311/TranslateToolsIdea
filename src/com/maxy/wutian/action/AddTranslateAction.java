package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.maxy.wutian.add.AddTranslateHelper;

import java.io.File;

public class AddTranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        String translatePath = Messages.showInputDialog(project,
                "Place Input Translate Path:",
                "Translate Path:",
                Messages.getQuestionIcon());

        File translateFile = new File(translatePath);
        if (!translateFile.exists())
            throw new RuntimeException("Translate File not exist: " + translateFile.getAbsolutePath());


        File projectFile = new File(project.getBasePath());
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper(projectFile, translateFile);
        addTranslateHelper.startAddTranslate();
    }
}
