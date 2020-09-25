package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.maxy.wutian.fix.FixAllTranslate;
import com.maxy.wutian.log.LogManager;

public class FixStringAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        FixAllTranslate fixAllTranslate = new FixAllTranslate();
        LogManager.getInstance().log("FixStringAction :: " + project.getBasePath());
        fixAllTranslate.start(project.getBasePath());
    }
}
