package com.maxy.wutian.add;


import com.maxy.wutian.bean.ILineEntity;
import com.maxy.wutian.bean.StrEntity;
import com.maxy.wutian.utils.FileReaderEx;
import com.maxy.wutian.utils.ReplaceSpecialCharUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AddTaskManager implements AddTranslateTask.AddTaskListener {
    private ExecutorService mExecutors;
    private Map<String, List<ILineEntity>> mValuesData = new HashMap<>();
    private List<AddTranslateTask> mTaskList = new ArrayList<>();

    public AddTaskManager() {
        mExecutors = Executors.newFixedThreadPool(10);
    }

    public void start(File translateResFile, File resDir) {
        startCheckPathIsRight(translateResFile);
        startAddTranslate(translateResFile, resDir);
    }

    private boolean startCheckPathIsRight(File translateResFile) {
        File[] valueDirArrays = translateResFile.listFiles();
        for (File valueDir : valueDirArrays) {
            if (valueDir.isHidden())
                continue;
            if (!valueDir.getName().startsWith("values"))
                throw new RuntimeException("Translate File Path Error!");

            for (File file : valueDir.listFiles()) {
                if (file.isDirectory()) {
                    //has error file
                    System.out.println(file.getAbsolutePath());
                    throw new RuntimeException("Translate File Path Error!");
                }
            }
        }
        return true;
    }

    private void startAddTranslate(File translateFileDir, File originDir) {
        if (originDir == null || !originDir.exists())
            return;

        if (translateFileDir == null || !translateFileDir.exists())
            return;

        for (File translateFile : translateFileDir.listFiles()) {
            if (translateFile.isHidden())
                continue;
            if (translateFile.isDirectory()) {
                File origin = new File(originDir, translateFile.getName());
                if (!origin.exists()) {
                    origin.mkdir();
                }
                startAddTranslate(translateFile, origin);
            } else {
                readValuesFile(translateFile, originDir);
                AddTranslateTask translateTask = new AddTranslateTask(this, translateFile, originDir);
                mTaskList.add(translateTask);
                mExecutors.submit(translateTask);
            }
        }
    }

    private void readValuesFile(File translateFile, File originDir) {
        String translateFileName = translateFile.getName();
        String mapKey = getMapKey(originDir, translateFileName);
        if (mValuesData.containsKey(mapKey)) {
            System.out.println(mapKey + "                          " + translateFile.getAbsolutePath());
            return;
        }

        File resDir = originDir.getParentFile();
        if (!resDir.getName().equals("res"))
            throw new RuntimeException("can not find res Dir :" + resDir.getAbsolutePath());

        File valuesDir = new File(resDir, "values");
        if (valuesDir == null || !valuesDir.exists())
            throw new RuntimeException("can not find values Dir :" + valuesDir.getAbsolutePath());

        File valueFile = new File(valuesDir, translateFileName);
        if (valueFile == null || !valueFile.exists())
            throw new RuntimeException(translateFile.getAbsolutePath() + "     " + "can not find values file :" + valueFile.getAbsolutePath());
        List<ILineEntity> lines = FileReaderEx.readFileAllString(valueFile, false);
        mValuesData.put(mapKey, lines);
    }

    @Override
    public void doAddTranslate(File translateFile, File originDir) {
        File valueXXFile = new File(originDir, translateFile.getName());
        if (!valueXXFile.exists()) {
            try {
                valueXXFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<ILineEntity> lines = mValuesData.get(getMapKey(originDir, translateFile.getName()));
        if (lines.isEmpty())
            throw new RuntimeException("can not get values file: " + translateFile.getName());

        if (!lines.isEmpty()) {
            Map<String, ILineEntity> transMap = FileReaderEx.readFileAllStringToMap(translateFile, false);
            Map<String, ILineEntity> valueXXMap = FileReaderEx.readFileAllStringToMap(valueXXFile, false);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueXXFile)))) {
                for (ILineEntity entity : lines) {
                    String line = entity.getLineText();
                    if (entity instanceof StrEntity) {
                        StrEntity strEntity = (StrEntity) entity;
                        String stringKey = strEntity.getStringKey();
                        if (transMap.containsKey(stringKey)) {
                            line = transMap.get(stringKey).getLineText();
                            line = ReplaceSpecialCharUtils.replaceSpecialChar(line);
                        } else if (valueXXMap.containsKey(stringKey)) {
                            line = valueXXMap.get(stringKey).getLineText();
                        } else
                            line = null;
                    }
                    if (line == null)
                        continue;
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void tryRelease() {
        endThreadPool();
    }

    private void endThreadPool() {
        if (mTaskList.isEmpty())
            return;
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int i = 0;
                    try {
                        for (AddTranslateTask task : mTaskList) {
                            if (task.isDone()) {
                                i++;
                                continue;
                            } else
                                break;
                        }

                        if (i == mTaskList.size() && mExecutors != null) {
                            mExecutors.shutdownNow();
                            mExecutors = null;
                            System.out.println("      FixedThreadPool.shutdownNow()      ");
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private String getMapKey(File originDir, String fileName) {
        if (!originDir.getAbsolutePath().contains("res"))
            System.out.println(originDir.getAbsolutePath());
        return getResPath(originDir)  + "______" + fileName;
    }

    private String getResPath(File originDir) {
        if (originDir.getName().equals("res"))
            return originDir.getAbsolutePath();
        return getResPath(originDir.getParentFile());
    }
}
