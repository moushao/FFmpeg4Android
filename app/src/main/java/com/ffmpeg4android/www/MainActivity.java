package com.ffmpeg4android.www;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String[] s = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission
            .WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission
            .INTERNET, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
    private Button mButton, mButton1, mButton2, mButton4;
    private ImageView mImageView;
    private String textAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取权限
        ActivityCompat.requestPermissions(this, s, 0);
        initView();
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.btn);
        mButton1 = (Button) findViewById(R.id.btn1);
        mButton2 = (Button) findViewById(R.id.btn2);
        mButton4 = (Button) findViewById(R.id.btn4);
        mImageView = (ImageView) findViewById(R.id.image);

        mButton.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        
        //videoAbsolutePath = "/storage/emulated/0/video/1512023240751.mp4";
    }

    private File videoFile;
    private String videoAbsolutePath;

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn:
                //获取路径
                String path = Environment.getExternalStorageDirectory() + File.separator + "video";
                //定义文件名
                String fileName = new Date().getTime() + ".mp4";
                videoFile = new File(path, fileName);
                //文件夹不存在
                if (!videoFile.getParentFile().exists()) {
                    videoFile.getParentFile().mkdirs();
                }
                Uri videoUri = Uri.fromFile(videoFile);
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn1:
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate=new Date(System.currentTimeMillis());
                String date=format.format(curDate);
                String s = date+"  我爱吃饺子";
                //将文本转换成图片
                setTextChangeImage(s);
                break;
            case R.id.btn2:
                if (videoAbsolutePath == null) {
                    Toast.makeText(this, "视频不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("path", videoAbsolutePath);
                intent.putExtra("imagePath", textAbsolutePath);
                startActivity(intent);
                break;
            case R.id.btn4:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    videoAbsolutePath = videoFile.getAbsolutePath();
                    Log.e("MainActivity", videoAbsolutePath);
                    break;
                default:
                    break;
            }
        }
    }
    private void setTextChangeImage(String s) {
        //设置画布的长宽
        int height = s.length() * 2;
        int width = s.length() * 30;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //设置画布
        Canvas canvas = new Canvas(bitmap);
        //背景颜色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //设置画笔
        Paint paint = new Paint();
        //设置文字居中
        paint.setTextAlign(Paint.Align.CENTER);
        //设置画笔颜色
        paint.setColor(Color.RED);
        //设置画笔粗细
        paint.setTextSize(40);
        //设置最终的文字位置
        float x = width / 2;
        float y = height / 2;
        canvas.drawText(s, x, y + 10, paint);
        //保存Bitmap
        saveBitmap(bitmap, s);
    }
    private void saveBitmap(Bitmap bitmap, String s) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "text", s);
        //文件夹不存在
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.e("Bitmap", "已经保存");
        } catch (IOException e) {
            e.printStackTrace();
        }
        textAbsolutePath = file.getAbsolutePath();
        mImageView.setImageBitmap(bitmap);
    }
}
