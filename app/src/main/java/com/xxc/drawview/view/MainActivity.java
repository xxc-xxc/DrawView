package com.xxc.drawview.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xxc.drawview.R;
import com.xxc.drawview.databinding.ActivityMainBinding;
import com.xxc.drawview.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    // 画笔粗细弹窗是否打开
    private boolean isPaintSizeDialogOpen = false;
    // 调色板弹窗是否打开
    private boolean isPaletteDialogOpen = false;
    // 画笔大小选择
    private final List<ImageView> mPaintSizeList = new ArrayList<>();
    // 画笔颜色选择
    private final List<ImageView> mPaletteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
//        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initData();
        setClickEvent();
    }

    private void initData() {
        mPaintSizeList.add(mBinding.ivSizeS);
        mPaintSizeList.add(mBinding.ivSizeM);
        mPaintSizeList.add(mBinding.ivSizeL);
        mPaintSizeList.add(mBinding.ivSizeXl);

        mPaletteList.add(mBinding.ivRed);
        mPaletteList.add(mBinding.ivGreen);
        mPaletteList.add(mBinding.ivBlue);
        mPaletteList.add(mBinding.ivYellow);
    }

    /**
     * 处理点击事件
     */
    private void setClickEvent() {
        // 点击画笔打开画笔大小选择框
        mBinding.ivPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaintSizeDialog();
            }
        });
        // 画笔大小选择
        mPaintSizeList.forEach(new Consumer<ImageView>() {
            @Override
            public void accept(ImageView imageView) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isPaintSizeDialogOpen) {
                            openPaintSizeDialog();
                        }
                        mBinding.drawView.setPaintSize(Float.parseFloat(imageView.getTag().toString()));
                    }
                });
            }
        });

        // 调色板颜色选择
        mBinding.ivPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaintSizeDialogOpen) {
                    openPaintSizeDialog();
                }
                openPaletteDialog();
            }
        });

        mPaletteList.forEach(new Consumer<ImageView>() {
            @Override
            public void accept(ImageView imageView) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = imageView.getTag().toString();
                        Log.d("cxx", "onClick: " + tag);
                        mBinding.drawView.setPaintColor(imageView.getTag().toString());
                        openPaletteDialog();
                    }
                });
            }
        });

        // 保存图片
        mBinding.ivSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaintSizeDialogOpen) {
                    openPaintSizeDialog();
                }
                capturePhoto();
            }
        });

        // 撤销
        mBinding.ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaintSizeDialogOpen) {
                    openPaintSizeDialog();
                }
                mBinding.drawView.undo();
            }
        });

        // 橡皮擦
        mBinding.ivEra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaintSizeDialogOpen) {
                    openPaintSizeDialog();
                }
                mBinding.drawView.setEar();
            }
        });

    }

    /**
     * 画笔大小选择弹窗
     */
    private void openPaintSizeDialog() {
        float startY = 1;
        float endY = 0;
        if (isPaintSizeDialogOpen) {
            // 关闭
            startY = 0;
            endY = 1;
            mBinding.paintSizeContainer.setVisibility(View.GONE);
        } else {
            // 打开
            mBinding.paintSizeContainer.setVisibility(View.VISIBLE);
        }
        TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.RELATIVE_TO_SELF, startY,
                Animation.RELATIVE_TO_SELF, endY);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        mBinding.paintSizeContainer.setAnimation(animation);
        isPaintSizeDialogOpen = !isPaintSizeDialogOpen;
    }

    /**
     * 打开调色板弹窗
     */
    private void openPaletteDialog() {
        if (isPaletteDialogOpen) {
            paletteDoAnimate(View.GONE, 60);
        } else {
            paletteDoAnimate(View.VISIBLE, -60);
        }
        isPaletteDialogOpen = !isPaletteDialogOpen;
    }

    /**
     * 调色板动画弹窗
     * @param status
     * @param index
     */
    private void paletteDoAnimate(int status, int index) {
        for (int i = 0; i < mPaletteList.size(); i++) {
            ImageView imageView = mPaletteList.get(i);
            imageView.setVisibility(status);
            imageView.animate()
                    .translationYBy(DisplayUtil.dpTopx(MainActivity.this, index * (i + 1)))
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator())
                            .start();
        }
    }

    /**
     * 以静态图像模式启动相机应用
     * @param targetFilename
     */
    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                Uri.withAppendedPath(locationForPhotos, targetFilename));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}