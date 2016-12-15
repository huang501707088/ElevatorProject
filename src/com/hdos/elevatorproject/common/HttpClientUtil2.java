package com.hdos.elevatorproject.common;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;


/**
 * Created by xyb on 2016/5/12.
 */
public class HttpClientUtil2 {

	public final static String baseUrl = "http://14.17.77.85:6112";

    /** 设置服务器超时时间*/
    public static final int SERVER_TIME_OUT = 30000;

    public static final String CODE = "0000";
    /**
     * 安装服务器的地址
     */
    public final static String url = baseUrl+"/dtjyzdc/json.html";
    private final static String posturl = baseUrl+"/hbtc/UploadServletService";

    /**
     * 访问网络返回字符串
     *
     * @param params key value键值对
     * @param params key url 访问的地址
     * @return 返回json字符串
     */
    public static String getBaseResult(List<NameValuePair> params, String url) {
        // TODO Auto-generated method stub
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(url, SERVER_TIME_OUT);
        HttpPost httpRequest = new HttpPost(url);

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result += EntityUtils.toString(httpResponse.getEntity());
                Log.i("+++++++++++", result);
                return result;
            } else {
                return "";
            }
        } catch (Exception e) {
            // TODO: handle exceptionG
            Log.i("+++++error+++++", e.getMessage());
            return "";
        }
    }


    /**
     * 访问网络返回字符串
     *
     * @param url
     * @return
     */
    public static String getGetBaseResult(String url) {
        String result = "";
        HttpGet httpGet = new HttpGet(url);
        HttpContext context = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        try{
            //取得HttpClient对象
            HttpClient httpClient = new DefaultHttpClient();
            //这里的超时单位是毫秒。这里的http.socket.timeout相当于SO_TIMEOUT
            httpClient.getParams().setIntParameter(url,SERVER_TIME_OUT);
            //请求HttpClient，取得HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpGet,context);
            //请求成功
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //取得返回的字符串
                result = EntityUtils.toString(httpResponse.getEntity());
                //textView.setText(strResult);
                //saveSession(cookieStore);
            }else{
                Log.e("GetActivity", "ClientProtocolException");
            }
        }catch (ClientProtocolException e) {
            // TODO: handle exception
            Log.e("GetActivity", "ClientProtocolException");
            e.printStackTrace();
        }catch (IOException e) {
            // TODO: handle exception
            Log.e("GetActivity", "IOException");
            e.printStackTrace();
        }
        return result;
    }


// 
    /**
     * 访问网络返回字符串
     *
     * @param params
     * @return
     */
    public static String getBaseResult(List<NameValuePair> params) {
        // TODO Auto-generated method stub
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        // 请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
    // 读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpPost httpRequest = new HttpPost(url);

//            if(null != SystemConfig.sessionId){
//                String JSESSIONID = SystemConfig.sessionId;
//                httpRequest.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
//        }
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result += EntityUtils.toString(httpResponse.getEntity());
                Log.i("+++++++++++", result);
                return result;
            } else {
                return "";
            }
        } catch (Exception e) {
            // TODO: handle exceptionG
           // Log.i("+++++error+++++", e.getMessage());
            return "";
        }
    }

    /**
     * 通过post完成文件的上传
     */
    public static String postFile(String filePath) {
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        String postUrl = posturl +"/UploadServletService";
        HttpPost httpPost = new HttpPost(postUrl);
        try {
            // 需要上传的文件
           // String root = "D:/api/";
            //String fileName = "JDK6.0 中文文档.CHM";
            File uploadFile = new File(filePath);
            //定义FileEntity对象
            HttpEntity entity = new FileEntity(uploadFile,"");
            //为httpPost设置头信息
           httpPost.setHeader("filename", URLEncoder.encode(filePath, "utf-8"));//服务器可以读取到该文件名
           //httpPost.setHeader("Content-Length", String.valueOf(entity.getContentLength()));//设置传输长度
            httpPost.setEntity(entity); //设置实体对象

            // httpClient执行httpPost提交
            HttpResponse response = httpClient.execute(httpPost);
            // 得到服务器响应实体对象
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                System.out.println(EntityUtils.toString(responseEntity, "utf-8"));
                //result = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println("文件 " + filePath + "上传成功！");
                return result;
            } else {
                System.out.println("服务器无响应！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }


    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    /**
     * Android上传文件到服务端
     *
     * @param file 需要上传的文件
     * @return 返回响应的内容
     */
    public static String uploadFile(File file) {
        String result = null;
        String RequestURL = posturl;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if(res==200)
                 {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : " + result);
                 }
                 else{
                 Log.e(TAG, "request error");
                 }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private static final  byte[] more="&".getBytes();
    /**
     * 访问网络获取下单接口初始化订单
     * @param url
     * @param params
     * @param charSet
     * @return
     */
    public static String Read(String url,String[] params,String charSet){
        StringBuffer temp = new StringBuffer();
        try {
            HttpURLConnection uc = (HttpURLConnection)new URL(url).openConnection();
            StringBuilder str = new StringBuilder();
            if(params!=null && params.length > 0){
                uc.setRequestMethod("POST");
                uc.setDoOutput(true);
                OutputStream out=new BufferedOutputStream(uc.getOutputStream());
                str.append("?");
                for(int i=params.length-1;i>=0;i--){
                    String parameter=params[i];
	            	/*
	            	 *这句话是用来转译参数的，如果转译的参数的话，在action还得对应的去转回来哦……
	            	 **/
                    //String parameter=java.net.URLEncoder.encode(para[i],"utf-8");s
                    str.append(parameter);
                    out.write(parameter.getBytes(charSet));
                    if(i>0){
                        out.write(HttpClientUtil2.more);
                        str.append("&");
                    }
                }
                out.flush();
                out.close();
            }
            Log.w("URL", url + str.toString());
            InputStream in = new BufferedInputStream(uc.getInputStream());
            Reader rd = new InputStreamReader(in, charSet);
            int c = 0;
            while ((c = rd.read()) != -1) {
                temp.append((char) c);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.w("Read", temp.toString());
        return temp.toString();
    }



}
