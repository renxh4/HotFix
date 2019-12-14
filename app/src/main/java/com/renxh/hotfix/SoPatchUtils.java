package com.renxh.hotfix;

import android.content.Context;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class SoPatchUtils {


    public static void patch_so(Context context){
        try {
            String localPath =  "/sdcard/libnative-lib_patch.so";
            Log.v("mmm", "LazyBandingLib localPath:" + localPath);

            // 开辟一个输入流
            File inFile = new File(localPath);
            // 判断需加载的文件是否存在
            if (!inFile.exists()){
                System.loadLibrary("native-lib");
                return;
            }
            FileInputStream fis = new FileInputStream(inFile);

            File dir = context.getDir("libs", Context.MODE_PRIVATE);
            // 获取驱动文件输出流
            File soFile = new File(dir,"libnative-lib.so");
            if (!soFile.exists()) {
                Log.v("mmm", "### " + soFile.getAbsolutePath() + " is not exists");
                FileOutputStream fos = new FileOutputStream(soFile);
                Log.v("mmm", "FileOutputStream:" + fos.toString());

                // 字节数组输出流，写入到内存中(ram)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                // 从内存到写入到具体文件
                fos.write(baos.toByteArray());
                // 关闭文件流
                baos.close();
                fos.close();
            }
            fis.close();
            Log.v("mmm", "### System.load start");
            // 加载外设驱动
            System.load(soFile.getAbsolutePath());
            Log.v("mmm", "### System.load End");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


}
