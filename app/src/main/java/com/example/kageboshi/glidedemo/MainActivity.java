package com.example.kageboshi.glidedemo;

import android.Manifest;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.Target;


import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String IMG_URL = "http://106.15.186.113/group1/M00/00/00/rBO46VthXA-AShxHAARvEA6F83U026.jpg";
    private ImageView image_view;
    private final static String IMG_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private Button btn_download;
    private Button btn_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionCheck();
        image_view = ((ImageView) findViewById(R.id.image));
        btn_download = ((Button) findViewById(R.id.download));
        btn_clear = ((Button) findViewById(R.id.clear));

        btn_download.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        Glide.with(this).load(IMG_URL).into(image_view);

    }

    private void permissionCheck() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                Log.e("aaa", "permission ok");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path = getImagePath(IMG_URL);
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(), path, "name", "description");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Intent intentBroadcast = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                        sendBroadcast(intentBroadcast);
                    }
                }).start();
                break;
            case R.id.clear:
                clearAll();
                break;
        }


    }

    private void clearAll() {
        Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        getContentResolver().delete(uri,"",null);
        Intent intentBroadcast = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
        sendBroadcast(intentBroadcast);
    }


    private String getImagePath(String imgUrl) {
        String filePath = "";
        try {
            File file = Glide.with(this).load(imgUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            filePath = file.getAbsolutePath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
