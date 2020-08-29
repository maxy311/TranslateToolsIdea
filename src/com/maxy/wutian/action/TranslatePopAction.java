package com.maxy.wutian.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.maxy.wutian.dialog.SampleDialogWrapper;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TranslatePopAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
//        ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder();
//        componentPopupBuilder.createPopup().show();
        SampleDialogWrapper sampleDialogWrapper = new SampleDialogWrapper();
        sampleDialogWrapper.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("keyTyped");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("keyPressed");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("keyReleased");
                System.out.println(sampleDialogWrapper.getPath() + "   " + sampleDialogWrapper.getCompareDir());
            }
        });
        sampleDialogWrapper.showAndGet();
    }
}
