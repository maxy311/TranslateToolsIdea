package com.maxy.wutian.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.maxy.wutian.utils.FileDirUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SampleDialogWrapper extends DialogWrapper {
    private JTextField pathText;
    private JTextField tagText;
    private JTextField compareText;

    public SampleDialogWrapper() {
        super(true); // use current window as parent
        init();
        setTitle("Test DialogWrapper");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();

        JLabel pathLabel = new JLabel("输入输出路径:");
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
        JLabel tagLabel = new JLabel("Last Tag:");
        tagLabel.setLocation(0, y);
        tagText = new JTextField();
        tagText.setPreferredSize(new Dimension(200, 25));
        tagText.setLocation(50, y);
        dialogPanel.add(tagLabel);
        dialogPanel.add(tagText);

        JLabel compareName = new JLabel("比较目录:");
        compareName.setLocation(0, 2 * y);
        compareText = new JTextField();
        compareText.setText("values-in");
        compareText.setPreferredSize(new Dimension(200, 25));
        compareText.setLocation(50, 2 * y);
        dialogPanel.add(compareName);
        dialogPanel.add(compareText);

        return dialogPanel;
    }

    public String getPath() {
        return pathText.getText();
    }

    public String getLastTag() {
        return tagText.getText();
    }

    public String getCompareDir() {
        return compareText.getText();
    }
}