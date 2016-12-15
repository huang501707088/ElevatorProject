package com.hdos.elevatorproject.common;


import com.alibaba.fastjson.JSON;
import com.hdos.mode.Task;

/**
 * Created by xyb on 2016/11/15.
 */
public class JsonTool {

    /**
     * 获得任务列表的对象
     * @param jsonstring
     * @param cls
     * @param <t>
     * @return
     */
    public static <t> Task getTask(String jsonstring, Class<Task> cls) {
        Task t = null;
        try {
            t = JSON.parseObject(jsonstring, cls);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }

}
