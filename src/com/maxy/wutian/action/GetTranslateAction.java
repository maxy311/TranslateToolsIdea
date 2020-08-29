package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.maxy.wutian.dialog.SampleDialogWrapper;
import com.maxy.wutian.get.GetTranslateHelper;
import org.jetbrains.annotations.SystemIndependent;

import java.io.File;

public class GetTranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        printProjectInfo(project);
//        showDialog(project);

        getTranslate(project);
    }

    private void printProjectInfo(Project project) {
        String name = project.getName();   //TranslateTools
        @SystemIndependent String projectFilePath = project.getProjectFilePath();    ///Users/maxy/Android/IdeaPorjects/TranslateTools/.idea/misc.xml
        @SystemIndependent String basePath = project.getBasePath();   // /Users/maxy/Android/IdeaPorjects/TranslateTools

        System.out.println(name + "   " + projectFilePath + "    " + basePath);
        VirtualFile projectFile = project.getProjectFile();
        String projectFilePath1 = projectFile.getPath();  // /Users/maxy/Android/IdeaPorjects/TranslateTools/.idea/misc.xml
        String name1 = projectFile.getName();   //misc.xml
        System.out.println(name1 + "    " + projectFilePath1);
    }

    private void showDialog(Project project) {
        String text = project.getBasePath() + "    " + project.getName();

        Messages.showMessageDialog(project,
                "Hello, " + text + "!\n I am glad to see you.",
                "Information",
                Messages.getInformationIcon());
    }

    private void getTranslate(Project project) {
        SampleDialogWrapper sampleDialogWrapper = new SampleDialogWrapper();
        sampleDialogWrapper.showAndGet();
        String translatePath = sampleDialogWrapper.getPath();
        String lastTag = sampleDialogWrapper.getLastTag();
        String compareDir = sampleDialogWrapper.getCompareDir();
//        String outPutPath = Messages.showInputDialog(project,
//                "Place Input Output Path:",
//                "Output Path:",
//                Messages.getQuestionIcon());

        File projectFile = new File(project.getBasePath());
        File translateTargetFile = new File(translatePath);
        if (!translateTargetFile.exists())
            translateTargetFile = projectFile;

        if (!translateTargetFile.exists() || !translateTargetFile.isDirectory())
            throw new RuntimeException("Output Path not exist : " + translateTargetFile.getAbsolutePath() + "    " + translateTargetFile.isDirectory());

        if (!translateTargetFile.getName().contains("Translate")) {
            translateTargetFile = new File(translateTargetFile, project.getName() + "_Translate");
            translateTargetFile.mkdir();
        }

//        projectFile = new File("/Users/maxy/Android/workspace/SHAREit");
        System.out.println(translateTargetFile.getAbsolutePath() +"         " + lastTag +"         " + compareDir);
        GetTranslateHelper translateHelper = new GetTranslateHelper(projectFile, translateTargetFile, lastTag, compareDir);
        translateHelper.startGetTranslate();
    }
}
