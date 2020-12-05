package com.ndong.hwapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

  private PreviewView cameraPreview;
  private ImageButton btnCapture;
  private FrameLayout cameraContainer;
  private SeekBar sbResizeRect;
  private DrawView rectangle;
  private Camera camera;
  private Preview preview;
  private ImageCapture imageCapture;
  private CameraSelector cameraSelector;
  private Size screenSize;
  private static final float MIN_RECT_FACTOR = 0.4f;
  private static final float MAX_RECT_FACTOR = 0.9f;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cameraPreview = findViewById(R.id.cameraPreview);
    btnCapture = findViewById(R.id.btnCapture);
    cameraContainer = findViewById(R.id.cameraContainer);
    sbResizeRect = findViewById(R.id.sbResizeRect);

    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    screenSize = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);

    int minRectSize = (int) (MIN_RECT_FACTOR*screenSize.getWidth());
    rectangle = new DrawView(this, new Size(minRectSize, minRectSize));
    cameraContainer.addView(rectangle);

    sbResizeRect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      private final int minSize = (int) (MIN_RECT_FACTOR*screenSize.getWidth());
      private final int maxSize = (int) (MAX_RECT_FACTOR*screenSize.getWidth());
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int size = (int) (minSize + ((maxSize-minSize)/100.0)*progress);
        rectangle.setSize(new Size(size, size));
        cameraContainer.removeView(rectangle);
        cameraContainer.addView(rectangle);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
      startCamera();
    else ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);

    btnCapture.setOnClickListener(view -> {
      takePhoto();
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
      startCamera();
    else Toast.makeText(this, "Please accept the camera permission", Toast.LENGTH_LONG).show();
  }

  private void startCamera() {
    final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
    cameraProviderFuture.addListener(() -> {
      try {
        final ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().build();
        cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
      } catch (ExecutionException | InterruptedException e) {
        Log.e("Start camera", e.getMessage());
      }
    }, ContextCompat.getMainExecutor(this));
  }

  private void takePhoto() {
    try {
      imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
              Bitmap bitmap = ImageUtils.convertCaptureToBitmap(image);
              bitmap = ImageUtils.rotateBitmap(bitmap, 90);
              bitmap = ImageUtils.cropBitmapByRectangle(bitmap, screenSize, rectangle.getSize());
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
              Intent intent = new Intent(MainActivity.this, PredictCharacterActivity.class);
              intent.putExtra("image", byteArrayOutputStream.toByteArray());
              startActivity(intent);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
              super.onError(exception);
            }
          }
      );
    } catch( Exception e) {
      Log.e("Start camera", e.getMessage());
    }
  }
}