package com.xzxx.decorate.o2o.bean;

import com.amos.modulebase.bean.BaseBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 客户端查看师傅评价  文件输出
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2018/9/29
 * <br> Copyright: Copyright © 2018 suiji Technology. All rights reserved.
 */
public class MasterCommentFile extends BaseBean {

    //    //    busiId (string, optional): 业务id ,
    //    private String busiId = "";
    //    //    fileId (string, optional): 文件id ,
    //    private String fileId = "";
    //    //    orderId (string, optional): 订单id ,
    //    private String orderId = "";
    //    //    persistentId (string, optional): 图片截图进度id ,
    //    private String persistentId = "";
    //    type (string, optional): 文件类型0图片 1 视频 ,
    private String type = "";
    //    url (string, optional): 文件地址 ,
    private String url = "";
    //    videoCoverImg (string, optional): 视频封面图
    private String videoCoverImg = "";

    @Override
    protected void init(JSONObject jsonObject) throws JSONException {
        type=jsonObject.optString("type","");
        url=jsonObject.optString("url","");
        videoCoverImg=jsonObject.optString("videoCoverImg","");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoCoverImg() {
        return videoCoverImg;
    }

    public void setVideoCoverImg(String videoCoverImg) {
        this.videoCoverImg = videoCoverImg;
    }
}
