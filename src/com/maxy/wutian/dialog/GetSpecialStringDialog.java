package com.maxy.wutian.dialog;

import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;

public class GetSpecialStringDialog extends DialogWrapper {
    private JTextField pathText;
    private JTextField specialStringPath;
    public GetSpecialStringDialog() {
        super(true); // use current window as parent
        init();
        setTitle("Export String from SHAREit");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        JLabel pathLabel = new JLabel("Project Root Path:");
        pathLabel.setLocation(0, 0);
        pathText = new JTextField();
        pathText.setPreferredSize(new Dimension(200, 25));
        pathText.setLocation(50, 0);
        dialogPanel.add(pathLabel);
        dialogPanel.add(pathText);

        int y = 120;
        JLabel tagLabel = new JLabel("Special Module values path:");
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