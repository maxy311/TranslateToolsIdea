package com.maxy.wutian.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.maxy.wutian.utils.FileDirUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class InputTwoDialog extends DialogWrapper {
    private JTextField pathText;
    private JTextField specialStringPath;
    private String title1;
    private String title2;
    public InputTwoDialog(String title1, String title2) {
        super(true); // use current window as parent
        init();
        setTitle("Export String from SHAREit");
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();

        JLabel pathLabel = new JLabel(title1);
        pathLabel.setLocation(0, 0);
        pathText = new JTextField();
        File deskTopFile = FileDirUtils.getDeskTopFile();
        if (deskTopFile.exists())
            pathText.setText(deskTopFile.getAbsolutePath());
        pathText.setPreferredSize(new Dimension(200, 25));
        pathText.setLocation(50, 0);
        dialogPanel.add(pathLabel);
        dialogPanel.add(pathText);

        int y = 120;
        JLabel tagLabel = new JLabel(title2);
        tagLabel.setLocation(0, y);
        specialStringPath = new JTextField();
        specialStringPath.setPreferredSize(new Dimension(200, 25));
        specialStringPath.setLocation(50, y);
        dialogPanel.add(tagLabel);
        dialogPanel.add(specialStringPath);

        return dialogPanel;
    }

    public String getFirstInput() {
        return pathText.getText();
    }

    public String getSecondInput() {
        return specialStringPath.getText();
    }
}