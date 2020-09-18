package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.maxy.wutian.ieport.ImportSpecialString;

public class ImportSpecialAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        String specialPath = Messages.showInputDialog(project,
                "Import Resources Path:",
                "Import Resources:",
                Messages.getQuestionIcon());
        String projectPath = project.getBasePath();
        ImportSpecialString importSpecialString = new ImportSpecialString(projectPath, specialPath);
        importSpecialString.start();
    }
}
