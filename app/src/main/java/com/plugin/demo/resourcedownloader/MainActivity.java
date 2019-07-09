package com.plugin.demo.resourcedownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ding.learn.resdownloader.Constants;
import com.ding.learn.resdownloader.DownloadManager;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.api.DownloadCallBack;
import com.ding.learn.resdownloader.db.DataBaseManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mTextView;
    private ImageView mImageView;
    private Button mProgressView;
    private Button mPause;
    private Button mDelete;
    private Button mCancle;
    private Button mClear;
    private DownloadRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadManager.getInstance().init(MainActivity.this);

        File file = new File("/mnt/sdcard/download");
        if (!file.exists()) {
            file.mkdirs();
        }
        DownloadRequest.Builder builder = new DownloadRequest.Builder();
        builder.setUrl("https://img-blog.csdnimg.cn/20190706173513195.png")
                .setName("test.png")
                .setFolder(file);
        mRequest = builder.build();

        mTextView = findViewById(R.id.text);
        mImageView = findViewById(R.id.image);
        mProgressView = findViewById(R.id.progress);
        mPause = findViewById(R.id.pause);
        mDelete = findViewById(R.id.delete);
        mCancle = findViewById(R.id.cancle);
        mClear = findViewById(R.id.clear);

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseManager.getInstance().delete("https://img-blog.csdnimg.cn/20190706173513195.png");
            }
        });

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().cancle(mRequest);
                mProgressView.setText("正在下载0.0");
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/mnt/sdcard/download/test.png");
                if (file.exists()) {
                    file.delete();
                    Toast.makeText(MainActivity.this, "文件已删除", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(Constants.DING, "Activity---文件不存在");
                }
                mImageView.setImageBitmap(null);
            }
        });

        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().pause(mRequest);
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadManager.getInstance().download(mRequest, new DownloadCallBack() {
                    @Override
                    public void onStarted() {
                        mTextView.setText("开始下载");
                        Log.e(Constants.DING, "DownloadCallBack--onStarted");
                    }

                    @Override
                    public void onConnecting() {
                        Log.e(Constants.DING, "DownloadCallBack--onConnecting");
                        mTextView.setText("正在链接");
                    }

                    @Override
                    public void onConnected(long length, boolean isAllowRang) {
                        Log.e(Constants.DING, "DownloadCallBack--onConnected");
                        mTextView.setText("链接完成");
                    }

                    @Override
                    public void onProgressUpdate(long finished, long total, float percent) {
                        mProgressView.setText("正在下载" + percent);
                    }

                    @Override
                    public void onCompleted() {
                        Log.e(Constants.DING, "Activity---onCompleted");
                        mTextView.setText("下载完成");
                        Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/download/test.png", null);
                        if (bitmap != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onPaused() {
                        Log.e(Constants.DING, "Activity---onPaused");
                    }

                    @Override
                    public void onCancled() {
                        Log.e(Constants.DING, "Activity---onCancled");
                    }

                    @Override
                    public void onFailed() {
                        Log.e(Constants.DING, "Activity---onFailed");
                    }
                });
            }
        });
    }
}
