package com.xxc.drawview.bean;

import android.graphics.Path;

/**
 * @ClassName LinePath
 * @Description 保存的画笔和路径信息
 * @Author xxc
 * @Date 2024/4/18 23:28
 * @Version 1.0
 */
public class LinePath {

    private Path path;
    private int color;
    private float size;

    public LinePath() {
    }

    public LinePath(Path path, int color, float size) {
        this.path = path;
        this.color = color;
        this.size = size;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
