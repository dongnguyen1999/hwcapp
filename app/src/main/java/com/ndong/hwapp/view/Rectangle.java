package com.ndong.hwapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Size;
import android.view.View;

@SuppressWarnings("ALL")
public class Rectangle extends View {
  private int size;
  private int color;
  private Paint paint = new Paint();

  private static final int DEFAULT_COLOR = Color.RED;

  public Rectangle(Context context, int size, int color) {
    super(context);
    this.size = size;
    this.color = color;
  }

  public Rectangle(Context context, int size) {
    this(context, size, DEFAULT_COLOR);
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public void onDraw(Canvas canvas) {
    paint.setColor(Color.TRANSPARENT);
    paint.setStyle(Paint.Style.FILL);
    float leftx = (getLeft() + getRight()) / 2 - size/2;
    float topy =  (getTop() + getBottom()) / 2 - size/2;
    float rightx =  (getLeft() + getRight()) / 2 + size/2;
    float bottomy =  (getTop() + getBottom()) / 2 + size/2;
    // FILL
    canvas.drawRect(leftx, topy, rightx, bottomy, paint);
    paint.setStrokeWidth(10);
    paint.setColor(color);
    paint.setStyle(Paint.Style.STROKE);
    // BORDER
    canvas.drawRect(leftx, topy, rightx, bottomy, paint);
    super.onDraw(canvas);
  }

}
