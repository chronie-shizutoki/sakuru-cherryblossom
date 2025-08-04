package com.example.sakurublossom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CherryBlossomView extends View {
    private static final String TAG = "CherryBlossomView";

    private List<CherryBlossom> cherryBlossoms = new ArrayList<>();
    private Paint paint = new Paint();
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = true;
    private int width, height;
    private Bitmap cherryBlossomBitmap;

    // 控制粒子数量
    private static final int MAX_BLOSSOMS = 100;
    // 控制更新频率
    private static final long UPDATE_INTERVAL = 30;

    public CherryBlossomView(Context context) {
        super(context);
        init(context);
    }

    public CherryBlossomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CherryBlossomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 加载樱花图片
        cherryBlossomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_sakula);
        if (cherryBlossomBitmap == null) {
            Log.e(TAG, "Failed to load cherry blossom image");
        }
        // 启动粒子更新
        startUpdate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        // 初始化粒子
        initCherryBlossoms();
    }

    private void initCherryBlossoms() {
        cherryBlossoms.clear();
        for (int i = 0; i < MAX_BLOSSOMS; i++) {
            cherryBlossoms.add(createNewBlossom());
        }
    }

    private CherryBlossom createNewBlossom() {
        float x = random.nextFloat() * width;
        float y = random.nextFloat() * -height; // 从屏幕上方开始
        float size = 20 + random.nextFloat() * 30; // 随机大小
        float speedX = -1 + random.nextFloat() * 2; // 左右摆动
        float speedY = 1 + random.nextFloat() * 3; // 下落速度
        float rotation = random.nextFloat() * 360; // 随机旋转角度
        float rotationSpeed = -1 + random.nextFloat() * 2; // 旋转速度

        return new CherryBlossom(x, y, size, speedX, speedY, rotation, rotationSpeed);
    }

    private void startUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    updateCherryBlossoms();
                    invalidate(); // 重绘视图
                    startUpdate();
                }
            }
        }, UPDATE_INTERVAL);
    }

    private void updateCherryBlossoms() {
        for (int i = 0; i < cherryBlossoms.size(); i++) {
            CherryBlossom blossom = cherryBlossoms.get(i);
            blossom.update();

            // 如果粒子超出屏幕底部，重新生成
            if (blossom.y > height + blossom.size) {
                cherryBlossoms.set(i, createNewBlossom());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        canvas.drawARGB(255, 240, 240, 255);

        // 绘制所有樱花
        for (CherryBlossom blossom : cherryBlossoms) {
            blossom.draw(canvas);
        }
    }

    // 暂停动画
    public void pause() {
        if (isRunning) {
            isRunning = false;
            Log.d(TAG, "Animation paused");
        }
    }

    // 恢复动画
    public void resume() {
        if (!isRunning) {
            isRunning = true;
            startUpdate();
            Log.d(TAG, "Animation resumed");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
        Log.d(TAG, "View detached from window");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resume();
        Log.d(TAG, "View attached to window");
    }

    // 樱花粒子类
    private class CherryBlossom {
        float x, y; // 位置
        float size; // 大小
        float speedX, speedY; // 速度
        float rotation; // 旋转角度
        float rotationSpeed; // 旋转速度

        public CherryBlossom(float x, float y, float size, float speedX, float speedY, float rotation, float rotationSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speedX = speedX;
            this.speedY = speedY;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
        }

        public void update() {
            // 更新位置
            x += speedX;
            y += speedY;

            // 更新旋转
            rotation += rotationSpeed;

            // 简单的风力效果，使樱花左右摆动
            speedX += (random.nextFloat() - 0.5f) * 0.1f;

            // 限制水平速度
            if (speedX > 2) speedX = 2;
            if (speedX < -2) speedX = -2;
        }

        public void draw(Canvas canvas) {
            // 保存当前画布状态
            int saveCount = canvas.save();

            // 平移、缩放和旋转画布
            canvas.translate(x, y);
            canvas.rotate(rotation, size / 2, size / 2);
            canvas.scale(size / cherryBlossomBitmap.getWidth(), size / cherryBlossomBitmap.getHeight());

            // 绘制樱花图片
            canvas.drawBitmap(cherryBlossomBitmap, 0, 0, paint);

            // 恢复画布状态
            canvas.restoreToCount(saveCount);
        }
    }
}