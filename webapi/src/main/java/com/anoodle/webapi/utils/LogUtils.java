package com.anoodle.webapi.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

    private static final String TAG = "--------------------";

    private static final int LEVEL_NONE = 0;

    private static final int LEVEL_VERBOSE = 1;

    private static final int LEVEL_DEBUG = 2;

    private static final int LEVEL_INFO = 3;

    private static final int LEVEL_WARING = 4;

    private static final int LEVEL_ERROR = 5;

    private static final int mDebugLevel = LEVEL_ERROR;

    public static void v(String msg){
        if(mDebugLevel>=LEVEL_VERBOSE){
            Log.v(TAG,msg);
        }
    }

    public static void d(String msg){
        if(mDebugLevel>=LEVEL_DEBUG){
            Log.d(TAG,msg);
        }
    }

    public static void i(String msg){
        if(mDebugLevel>=LEVEL_INFO){
            Log.i(TAG,msg);
        }
    }

    public static void w(String msg){
        if(mDebugLevel>=LEVEL_WARING){
            Log.w(TAG,msg);
        }
    }

    public static void e(String msg){
        if(mDebugLevel>=LEVEL_ERROR){
            Log.e(TAG,msg);
        }
    }

    public static void v(String tag, String msg){
        if(mDebugLevel>=LEVEL_VERBOSE){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if(mDebugLevel>=LEVEL_DEBUG){
            Log.d(tag,msg);
        }
    }

    public static void i(String tag, String msg){
        if(mDebugLevel>=LEVEL_INFO){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag, String msg){
        if(mDebugLevel>=LEVEL_WARING){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if(mDebugLevel>=LEVEL_ERROR){
            Log.e(tag,msg);
        }
    }

    private static String MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory() + File.separator + "voiceLog";// 日志文件在sdcard中的路径
    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称

    public static String writeLogtoFile(String text) {// 新建或打开日志文件
        SimpleDateFormat logfile = new SimpleDateFormat(DateUtil.STYLE1);// 日志文件格式
        Date nowtime = new Date();
        String needWriteFile = logfile.format(nowtime);

        File dirsFile = new File(MYLOG_PATH_SDCARD_DIR);
        if (!dirsFile.exists()){
            dirsFile.mkdirs();
        }

        String path = dirsFile.toString() + File.separator +  needWriteFile + MYLOGFILEName;
        File file = new File(dirsFile.toString(), needWriteFile + MYLOGFILEName);// MYLOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

}
