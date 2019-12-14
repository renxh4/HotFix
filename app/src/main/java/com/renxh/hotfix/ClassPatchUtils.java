package com.renxh.hotfix;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassPatchUtils {
    public static void patch_class(Context context) {
        try {
            //获取ClassLoader
            ClassLoader classLoader = MainActivity.class.getClassLoader();
            //获取BaseDexClassLoader的class对象
            Class<?> superclass = classLoader.getClass().getSuperclass();
            //反射获取BaseDexClassLoader的变量pathList
            Field baseDexpathList = superclass.getDeclaredField("pathList");
            //设置可访问
            baseDexpathList.setAccessible(true);
            //获取当前内存中的PathClassLoader的pathList对象
            Object pathlist = baseDexpathList.get(classLoader);
            //获取DexpathList中的dexElements成员变量
            Field dexElementsFiled = pathlist.getClass().getDeclaredField("dexElements");
            //设置可访问
            dexElementsFiled.setAccessible(true);
            //获取当前pathList中的dexElements数组对象
            Object[] dexElements = (Object[]) dexElementsFiled.get(pathlist);
            //获取pathList中的makeDexElements方法
            Method makeDexElements = pathlist.getClass().getDeclaredMethod("makeDexElements",
                    List.class, File.class, List.class, ClassLoader.class);
            makeDexElements.setAccessible(true);
            //构建第一个参数，补丁包
            ArrayList<File> files = new ArrayList<>();
            //获取补丁包
            File file = new File(copyFile("/sdcard/patch_dex.jar", context));
            files.add(file);
            //构建第二个参数，指定解压目录
            File optimizedDirectory = new File(context.getFilesDir().getAbsolutePath() +
                    File.separator + "patch");
            if (!optimizedDirectory.exists()) {
                optimizedDirectory.mkdirs();
            }
            //构建第三个参数
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            //执行makeDexElements方法,获取补丁包的dexElement数组
            Object[] patchdexElements = (Object[]) makeDexElements.invoke(pathlist, files,
                    optimizedDirectory, suppressedExceptions, classLoader);
            //创建一个数组
            Object[] finalArray = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(),
                    dexElements.length + patchdexElements.length);
            //把补丁包放到刚创建的数组
            System.arraycopy(patchdexElements, 0, finalArray, 0, patchdexElements.length);
            //把原先的数组放入新建的数组
            System.arraycopy(dexElements, 0, finalArray, patchdexElements.length, dexElements.length);
            //把新数组替换掉原先的数组
            dexElementsFiled.set(pathlist, finalArray);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static String copyFile(String patchpath, Context context) {
        String src = context.getFilesDir().getAbsolutePath() + File.separator + "patch_dex.jar";
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(new File(patchpath));
            outputStream = new BufferedOutputStream(new FileOutputStream(src));
            byte[] temp = new byte[1024];
            int len;
            while ((len = (inputStream.read(temp))) != -1) {
                outputStream.write(temp, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return src;
    }
}
