package com.maxy.wutian.bean;

public class CommentEntity implements ILineEntity {
    private String lineText;

    public CommentEntity(String lineText) {
        this.lineText = lineText;
    }

    @Override
    public String getLineText() {
        return lineText;
    }

    @Override
    public boolean isNeedTranslate() {
        return false;
    }
}
