package com.amisha.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {

    SurfaceView sv;
    TextView t;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sv=findViewById(R.id.surfaceView);
        t=findViewById(R.id.textView);
         barcodeDetector=new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
         cameraSource=new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(480,480).build();
         sv.getHolder().addCallback(new SurfaceHolder.Callback() {
             @Override
             public void surfaceCreated(SurfaceHolder holder) {
                 //run-time permission

                 if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                 {
                     ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},0);
                     return;
                 }

                 try {
                     cameraSource.start();
                 }
                 catch (Exception e)
                 {

                 }
             }

             @Override
             public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

             }

             @Override
             public void surfaceDestroyed(SurfaceHolder holder) {

                 cameraSource.stop();

             }
         });

         barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
             @Override
             public void release() {

             }

             @Override
             public void receiveDetections(Detector.Detections<Barcode> detections)
             {
                 final SparseArray<Barcode> qrcode = detections.getDetectedItems(); //filter the values (any type of data)

                 if(qrcode.size()!=0)
                 {
                     t.post(new Runnable() {  // post-schedules the value every time we detect qrcode at run time
                         @Override
                         public void run() {

                             //for vibration
                             Vibrator vibrator =(Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
                             vibrator.vibrate(1000);
                             t.setText(qrcode.valueAt(0).displayValue);


                         }
                     });
                 }
                 else
                 {
                     Toast.makeText(MainActivity.this,"No Record Found",Toast.LENGTH_SHORT).show();
                 }

             }
         });

         t.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 String url=t.getText().toString();
                 if(!url.startsWith("http://") && !url.startsWith("https://"))
                     url = "http://" + url;

                 if(url.contains("www.")||url.contains(".com")||url.contains(".in")||url.contains(".live")) {
                     Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(t.getText().toString()));
                     startActivity(i);
                 }
                 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                 startActivity(browserIntent);
             }
         });
    }
}
