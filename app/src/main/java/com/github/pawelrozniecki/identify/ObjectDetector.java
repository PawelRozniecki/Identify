package com.github.pawelrozniecki.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.util.List;

public class ObjectDetector {
    MainActivity m = new MainActivity();


    public static final int CATEGORY_UNKNOWN = 0;
    public static final int CATEGORY_HOME_GOOD = 1;
    public static final int CATEGORY_FASHION_GOOD = 2;
    public static final int CATEGORY_FOOD = 3;
    public static final int CATEGORY_PLACE = 4;
    public static final int CATEGORY_PLANT = 5;
    FirebaseVisionObject a;
   private Context mContext;


    public ObjectDetector(Context mContext) {
        this.mContext = mContext;
    }

    protected void detectObjects(final Bitmap bitmap, final ImageView imageView) {

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();
        FirebaseVisionObjectDetector detector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options);
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {

            @Override
            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {
                Paint textPaint = new Paint();
                int scaledSize = mContext.getResources().getDimensionPixelSize(R.dimen.classFont);

                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setTextSize(scaledSize);
                textPaint.setColor(Color.WHITE);
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.RED);
                p.setStrokeWidth(8);

                p.setTextSize(scaledSize);

                Bitmap bmp_Copy = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(bmp_Copy);

                for (int i = 0; i <detectedObjects.size();i++) {
                    Integer id = detectedObjects.get(i).getTrackingId();
                    Rect bounds = detectedObjects.get(i).getBoundingBox();
                    int category = detectedObjects.get(i).getClassificationCategory();
                    canvas.drawRect(bounds,p);


                    switch(detectedObjects.get(i).getClassificationCategory()){
                        case 0: canvas.drawText("UNKNOWN", bounds.right,bounds.bottom,textPaint);break;
                        case 1: canvas.drawText("HOME_GOOD", bounds.right,bounds.bottom,textPaint);break;
                        case 2: canvas.drawText("FASHION_GOOD", bounds.right,bounds.bottom,textPaint);break;
                        case 3: canvas.drawText("FOOD", bounds.right,bounds.bottom,textPaint);break;
                        case 4: canvas.drawText("PLACE", bounds.right,bounds.bottom,textPaint);break;
                        case 5: canvas.drawText("PLANT", bounds.right,bounds.bottom,textPaint);break;
                    }
                }
                imageView.setImageBitmap(bmp_Copy);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
