package com.ndong.hwapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Size;
import android.view.View;

@SuppressWarnings("ALL")
public class DrawView extends View {
  private String shape;
  private Size size;
  private int color;
  private Paint paint = new Paint();

  public DrawView(Context context, String shape, Size size, int color) {
    super(context);
    this.shape = shape;
    this.size = size;
    this.color = color;
  }



  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public DrawView(Context context) {
    this(context, "rect", new Size(50,50), Color.BLACK);
  }

  public DrawView(Context context, Size size) {
    this(context, "rect", size, Color.RED);
  }

  public DrawView(Context context, Size size, int color) {
    this(context, "rect", size, color);
  }



  @Override
  public void onDraw(Canvas canvas) {
    if (shape.equals("rect")) {
      paint.setColor(Color.TRANSPARENT);
      paint.setStyle(Paint.Style.FILL);
      float leftx = (getLeft() + getRight()) / 2 - size.getWidth()/2;
      float topy =  (getTop() + getBottom()) / 2 - size.getHeight()/2;
      float rightx =  (getLeft() + getRight()) / 2 + size.getWidth()/2;
      float bottomy =  (getTop() + getBottom()) / 2 + size.getHeight()/2;
      // FILL
      canvas.drawRect(leftx, topy, rightx, bottomy, paint);
      paint.setStrokeWidth(10);
      paint.setColor(color);
      paint.setStyle(Paint.Style.STROKE);
      // BORDER
      canvas.drawRect(leftx, topy, rightx, bottomy, paint);
    }
    super.onDraw(canvas);
  }

}
