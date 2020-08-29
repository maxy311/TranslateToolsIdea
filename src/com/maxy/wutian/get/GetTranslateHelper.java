package com.maxy.wutian.get;


import com.maxy.wutian.bean.CompareBean;
import com.maxy.wutian.bean.ModuleBean;
import com.maxy.wutian.filter.StringFileFilter;
import com.maxy.wutian.utils.ModulePareHelper;
import com.maxy.wutian.utils.ShellUtils;

import java.io.File;

public class GetTranslateHelper {
    private File outFile;
    private File projectFile;
    private String projectName;
    private String basePath;
    private String lastTag;
    private String compareDir;

    public GetTranslateHelper(File projectFile, File outFile, String lastTag, String compareDir) {
        this.outFile = outFile;
        this.projectFile = projectFile;
        this.projectName = projectFile.getName();
        this.basePath = projectFile.getAbsolutePath();

        this.lastTag = lastTag;
        this.compareDir = compareDir;
    }

    public void startGetTranslate() {
        //1. delete origin shareit file;
        checkOutputFile();

        //read values file to map
        CompareBean valueCompareBean = new CompareBean("values");
        pareString(valueCompareBean, projectFile, valueCompareBean.getValueDir());

        CompareBean xxValueCompareBean = new CompareBean(!isEmptyStr(compareDir) ? compareDir : "values-ar");
        pareString(xxValueCompareBean, projectFile, xxValueCompareBean.getValueDir());

        //read last tag values file to map
        CompareBean preValueCompareBean = new CompareBean("values");
        if (!isEmptyStr(lastTag)) {
            File shellFile = new File(projectFile, "git-for-all.sh");

            if (shellFile.exists()) {
                ShellUtils.checkoutToTag(shellFile.getAbsolutePath(), lastTag);
                System.out.println("has checkout to:" + lastTag);
                pareString(preValueCompareBean, projectFile, xxValueCompareBean.getValueDir());
                ShellUtils.checkoutToTag(shellFile.getAbsolutePath(), "master");
            }
        }
        PrintTranslateUtils printTranslateUtils = new PrintTranslateUtils(outFile, valueCompareBean, xxValueCompareBean, preValueCompareBean);
        printTranslateUtils.outputTranslateFile();
    }

    private void pareString(CompareBean compareBean, File file, String valueDir) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles(new StringFileFilter(valueDir))) {
                pareString(compareBean, listFile, valueDir);
            }
        } else {
            if (!StringFileFilter.isStringsFile(file))
                return;

            if (!isValuesFile(file, valueDir))
                return;
            String fileModule = ModulePareHelper.parseFileToModuleName(projectFile, file);
            ModuleBean moduleBean = compareBean.getModuleBean(fileModule);
            moduleBean.parseFile(file);
        }
    }

    private boolean isValuesFile(File file, String valueDir) {
        if (valueDir == null)
            System.out.println("isValuesFile   " + file.getAbsolutePath() + "         " + valueDir);
        return valueDir.equals(file.getParentFile().getName());
    }

    private void checkOutputFile() {
        if (projectFile == null || !projectFile.exists() || outFile == null)
            throw new RuntimeException("GetTranslateHelper projectFile not exist :" + projectFile + "     " + outFile);

        if (!outFile.getName().contains("Translate"))
            outFile = new File(outFile, projectName + "_Translate");

        if (!outFile.exists())
            outFile.mkdir();

        removeAllSubFiles(outFile);
    }

    private void removeAllSubFiles(File desktopShareitFile) {
        if (desktopShareitFile.isDirectory()) {
            for (File listFile : desktopShareitFile.listFiles()) {
                removeAllSubFiles(listFile);
            }
        } else {
            desktopShareitFile.delete();
        }
    }

    private boolean isEmptyStr(String str) {
        return str == null || str.trim().length() == 0;
    }

}
