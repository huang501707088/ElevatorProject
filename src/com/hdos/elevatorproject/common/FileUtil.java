package com.hdos.elevatorproject.common;

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xyb on 2016/9/16.
 */
public class FileUtil {

    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    private void read(String fileName,Context context) {
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    public static String readAsset(String fileName,Context context) {
        String result="";
        try {
            InputStream inputStream=context.getResources().getAssets().open(fileName);
            byte[] buffer=new byte[inputStream.available()];
            inputStream.read(buffer);
            result=new String(buffer,"GB2312");
           /* InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            while((line = bufReader.readLine()) != null)
                result  += line;*/
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读写/data/data/<应用程序名>目录上的文件:
     * @param fileName
     * @param context
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName,Context context) throws IOException{
        String res="";
        try{
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;

    }

    /**
     * 写/data/data/<应用程序名>目录上的文件
     * @param fileName
     * @param writestr
     * @param context
     * @throws IOException
     */
    public static void writeFile(String fileName,String writestr,Context context) throws IOException{
        try{

            FileOutputStream fout = context.openFileOutput(fileName, context.MODE_PRIVATE);

            byte [] bytes = writestr.getBytes();

            fout.write(bytes);

            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 写入私有文件
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void writeFileString(String fileName, String content) {

        FileWriter fout = null;
        try {
            fout = new FileWriter(fileName);
            fout.write(content);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
