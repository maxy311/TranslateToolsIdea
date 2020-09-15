package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.maxy.wutian.dialog.InputTwoDialog;
import com.maxy.wutian.ieport.GetSpecialString;

public class GetSpecialStringAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        InputTwoDialog getSpecialStringDialog = new InputTwoDialog("SHAREit path:", "Special Key path:");
        getSpecialStringDialog.showAndGet();
        String shareitPath = getSpecialStringDialog.getFirstInput();
        String inputPath = getSpecialStringDialog.getSecondInput();
        GetSpecialString getSpecialString = new GetSpecialString(project.getName(), shareitPath, inputPath);
        getSpecialString.start();
    }
}
