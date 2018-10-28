package com.paraxco.commontools.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.logging.Logger;


public class SmartLogger {
    private static SmartLogger instance;
    public static SmartLogger Companion;//this is just for compatibility issue after converted from kotlin

    public static SmartLogger getInstance() {
        if (instance == null) {
            instance = new SmartLogger();
            Companion = instance;
        }
        return instance;
    }

    public static void logDebug(String msg) {
        getInstance().instanceLogDebug(msg);
    }

    public static void logError(String msg) {
        getInstance().instanceLogDebug(msg);
    }


    public static void logDebug() {
        getInstance().instanceLogDebug("");
    }

    private static Context context = null;
    private static String classPrifix = "";

    private String LOGGER_NAME = "SmartLogger";

    public static void initLogger(Context contextValue) {
        context = contextValue;
    }

    public static void setClassPrefix(String prefix) {
        classPrifix = prefix;
    }

    public static void releaseContext() {
        context = null;
    }

    public void instanceLogDebug(String msg) {
        if (getElement().getClassName().startsWith(classPrifix))
            Logger.getLogger(LOGGER_NAME).warning(" \n" + getLogDivider() + "\n" + getHeaders() + "\n" + msg + "\n" + getLogDivider());
//        Logger.getLogger("fg").log(Level.ALL,"l");
//        Logger.getLogger("fg").log(Level.ALL,"l");
    }

    private String getHeaders() {
        //todo add real instance class name
        StackTraceElement element = getElement();
        String className = element.getClassName();
        String methodName = element.getMethodName();
        String lineNumber = String.valueOf(element.getLineNumber());
        return "Class:  " + className + "\n" + "Method: " + methodName + "(Line: " + lineNumber + ") Version:" + getVersion() + "\n" + getMessageDivider();
    }

    private String getMessageDivider() {
        return "**************************************************************************************************************";
    }

    private String getLogDivider() {
        return "--------------------------------------------------------------------------------------------------------------";
    }

    private StackTraceElement getElement() {
        boolean inThis = false;
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            //find first element witch is not this class
            if (stackTraceElement.getClassName().endsWith(SmartLogger.class.getName()))
                inThis = true;
            else if (inThis) {
                return stackTraceElement;
            }
        }

        return Thread.currentThread().getStackTrace()[4];
    }


    private String getVersion() {
        if (context == null)
            return "No version(Not initialized use initLogger to initialize!)";
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);

//            Toast.makeText(this,
//                    "PackageName = " + info.packageName + "\nVersionCode = "
//                            + info.versionCode + "\nVersionName = "
//                            + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show()
            String versionCode = String.valueOf(info.versionCode);
            String versionName = info.versionName;
            return versionName + "(" + versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "PackageManager.NameNotFoundException";
    }


}