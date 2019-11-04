package com.xzxx.decorate.o2o.bean;

import android.content.Context;
import android.text.TextUtils;

import com.amos.modulebase.bean.BaseBean;
import com.amos.modulebase.utils.RandomUtil;
import com.xzxx.decorate.o2o.adapter.CommentImageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * 客户端查看师傅评价
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2018/9/29
 * <br> Copyright: Copyright © 2018 suiji Technology. All rights reserved.
 */
public class MasterCommentInfoVo extends BaseBean {
    //    anonymous (string, optional): 匿名0 匿名 1 实名 ,
    private String anonymous = "";
    //    commentId (string, optional): 评论id ,
    private String commentId = "";
    //    commentTime (string, optional): 评论时间 ,
    private String commentTime = "";
    //    content (string, optional): 评论内容 ,
    private String content = "";
    //    customerHeadImg (string, optional): 用户头像 ,
    private String customerHeadImg = "";
    //    customerId (string, optional):  用户id ,
    private String customerId = "";
    //    customerName (string, optional): 用户姓名 ,
    private String customerName = "";
    //    orderScore (string, optional): 订单分数 ,
    private String orderScore = "";
    //    secondItemName (string, optional): 二级类目名称
    private String secondItemName = "";
    //    orderFiles (Array[订单文件输出], optional): 评论文件 ,
    private ArrayList<MasterCommentFile> orderFiles = new ArrayList<>();

    private CommentImageAdapter adapter;

    @Override
    public void init(JSONObject jsonObject) throws JSONException {
        anonymous = jsonObject.optString("anonymous", "");
        commentId = jsonObject.optString("commentId", "");
        commentTime = jsonObject.optString("commentTime", "");
        content = jsonObject.optString("content", "");
        customerHeadImg = jsonObject.optString("customerHeadImg", "");
        customerId = jsonObject.optString("customerId", "");
        customerName = jsonObject.optString("customerName", "");
        orderScore = jsonObject.optString("orderScore", "");
        secondItemName = jsonObject.optString("secondItemName", "");
        orderFiles = (ArrayList<MasterCommentFile>) BaseBean.toModelList(jsonObject.optString("orderFiles"), MasterCommentFile.class);
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCustomerHeadImg() {
        return customerHeadImg;
    }

    public void setCustomerHeadImg(String customerHeadImg) {
        this.customerHeadImg = customerHeadImg;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderScore() {
        return orderScore;
    }

    public void setOrderScore(String orderScore) {
        this.orderScore = orderScore;
    }

    public String getSecondItemName() {
        return secondItemName;
    }

    public void setSecondItemName(String secondItemName) {
        this.secondItemName = secondItemName;
    }

    public ArrayList<MasterCommentFile> getOrderFiles() {
        return orderFiles;
    }

    public void setOrderFiles(ArrayList<MasterCommentFile> orderFiles) {
        this.orderFiles = orderFiles;
    }

    public CommentImageAdapter getAdapter(Context context) {
        if (adapter == null) {
            adapter = new CommentImageAdapter(context, orderFiles, 1);
        }
        return adapter;
    }

    public void setAdapter(CommentImageAdapter adapter) {
        this.adapter = adapter;
    }

    public void initTest(Context context) {
        String string = "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p511118051.jpg" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p636048104.jpg" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2201168652.jpg" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2201168652.jpg" +
                ",http://tb-video.bdstatic.com/tieba-smallvideo-transcode/8206773_3ab3aab22f6634988ff7f91985a9f744_3.mp4" +
                ",http://tb-video.bdstatic.com/tieba-smallvideo-transcode/1867068_760690c177afb5698920f100037d43fe_3.mp4" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1910900710.jpg" +
                ",http://tb-video.bdstatic.com/tieba-smallvideo-transcode/2484951_6c4c050250b85aa7aaa83c11ff74bf5e_2.mp4" +
                ",http://tb-video.bdstatic.com/tieba-smallvideo-transcode/42_9edbffa49d8e033e79316ab49f04f314_2.mp4" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p456777022.jpg" +
                ",http://tb-video.bdstatic.com/tieba-smallvideo-transcode/16_40d106ea4edcd314d41213f203289cf6_1.mp4" +
                //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1538646661.jpg" +
                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p647422117.jpg";

        //        String[] strs = string.split(",");
        //        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(strs));
        String[] strs = string.split(",");
        int size = RandomUtil.getRandom(1, 7);
        orderFiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MasterCommentFile bean = new MasterCommentFile();
            bean.setUrl(strs[i]);
            bean.setVideoCoverImg("https://img1.doubanio.com/view/photo/s_ratio_poster/public/p647422117.jpg");
            bean.setType(isVideoFile(strs[i]) ? "1" : "0");
            orderFiles.add(bean);
        }
        adapter = new CommentImageAdapter(context, orderFiles, 1);
    }

    private boolean isVideoFile(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        String PREFIX_VIDEO = "video/";
        return !TextUtils.isEmpty(type) && type.contains(PREFIX_VIDEO);
    }
}
