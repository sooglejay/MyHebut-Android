package com.myhebut.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonCommon;
import com.myhebut.entity.User;
import com.myhebut.manager.SettingManager;
import com.myhebut.utils.HebutUtil;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingAvatarActivity extends SwipeBackActivity {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private OSS oss;

    private String userName;

    private String avatar;

    private User user;

    private HttpUtils http;

    private Gson gson;

    private Bitmap photo;

    private SettingManager manager;

    @ViewInject(R.id.iv_setting_personal_avatar)
    private ImageView mIvAvatar;

    @ViewInject(R.id.ll_setting_avatar_waiting)
    private LinearLayout mLlWaiting;

    @OnClick(R.id.btn_setting_personal_avatar)
    private void getPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personal_avatar);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    public void initData() {
        manager = SettingManager.getInstance();

        gson = new Gson();
        http = HttpUtil.getHttp();
        // 设置个人信息
        MyApplication application = (MyApplication) getApplication();
        user = application.getUser();
        userName = user.getUserName();
        String url = UrlUtil.getAvatarUrl(user.getAvatar());
        Picasso.with(this).load(url).placeholder(R.mipmap.user_avatar_default).
                error(R.mipmap.user_avatar_default).into(mIvAvatar);
        // 初始化OSS
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // OSS参数()
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("****", "****");
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY://当选择从本地获取图片时
                //做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data != null)
                    startPhotoZoom(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT://返回的结果
                if (data != null)
                    setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    @SuppressWarnings("deprecation")
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            photo = bundle.getParcelable("data");
            uploadPhoto();
        }
    }

    private void uploadPhoto() {
        avatar = userName + HebutUtil.getDate4Save();
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("myhebut", "touxiang/" + avatar + ".jpg", Bitmap2Bytes(photo));

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        // 显示等待画面
        mLlWaiting.setVisibility(View.VISIBLE);

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                // 更新头像地址
                updateAvatarUrl();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    private void updateAvatarUrl() {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", user.getUserId() + "");
        params.addBodyParameter("avatar", avatar);
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.updateAvatarUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);

                    mIvAvatar.setImageBitmap(photo);
                    // 隐藏等待画面
                    mLlWaiting.setVisibility(View.GONE);
                    // 修改全局变量
                    user.setAvatar(avatar);
                    // 接口回调更新
                    manager.sendInitDataAgain();

                    Toast.makeText(SettingAvatarActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                } catch(Exception e){
                    Toast.makeText(SettingAvatarActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(SettingAvatarActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}



