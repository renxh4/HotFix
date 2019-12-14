package com.renxh.hotfix;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        RequestPermissionsUtlis requestPermissionsUtlis = new RequestPermissionsUtlis(this);
        requestPermissionsUtlis.requestPermissions(99);
    }

    private void initView() {
        mImageView = findViewById(R.id.image);
        Button code = findViewById(R.id.code);
        Button patch_code = findViewById(R.id.patch_code);
        Button resource = findViewById(R.id.resource);
        Button patch_resource = findViewById(R.id.patch_resource);
        Button so = findViewById(R.id.so);
        Button patch_so = findViewById(R.id.so_patch);
        code.setOnClickListener(this);
        patch_code.setOnClickListener(this);
        resource.setOnClickListener(this);
        patch_resource.setOnClickListener(this);
        so.setOnClickListener(this);
        patch_so.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.code:
                Toast.makeText(MainActivity.this, Text.message(), Toast.LENGTH_LONG).show();
                break;
            case R.id.patch_code:
                ClassPatchUtils.patch_class(MainActivity.this);
                break;
            case R.id.resource:
                mImageView.setImageResource(R.mipmap.aaaa);
                break;
            case R.id.patch_resource:
                ArrayList<Activity> activities = new ArrayList<>();
                activities.add(MainActivity.this);
                ResourcePatchUtils.monkeyPatchExistingResources(MainActivity.this,
                        "/sdcard/patch.apk", activities);
                break;
            case R.id.so:
                Toast.makeText(MainActivity.this, SoUtils.getKey(), Toast.LENGTH_LONG).show();
                break;
            case R.id.so_patch:
                SoPatchUtils.patch_so(MainActivity.this);
                break;
        }
    }


}
