package com.maxy.wutian.add;

import java.io.File;

public class AddTranslateTask implements Runnable {

    private File mTranslateFile;
    private File mOriginFile;
    private AddTaskListener addTaskListener;
    protected boolean mIsDone = false;

    public AddTranslateTask(AddTaskListener addTaskListener,File translateFile, File originFile) {
        this.addTaskListener = addTaskListener;
        mTranslateFile = translateFile;
        mOriginFile = originFile;
    }

    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public void run() {
        mIsDone = false;
        if (addTaskListener != null)
            addTaskListener.doAddTranslate(mTranslateFile, mOriginFile);

        mIsDone = true;
    }

    public interface AddTaskListener{
        void doAddTranslate(File translateFile, File originFile);
    }
}
