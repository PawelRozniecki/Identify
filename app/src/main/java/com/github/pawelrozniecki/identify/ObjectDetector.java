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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;

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
        FirebaseVisionObjectDetector detector = FirebaseVision.getInstance().getOnDeviceObjectDetector();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {

            @Override
            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {

                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.RED);
                p.setStrokeWidth(3);
                int scaledSize = mContext.getResources().getDimensionPixelSize(R.dimen.classFont);
                p.setTextSize(scaledSize);

                Bitmap bmp_Copy = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(bmp_Copy);

                for (int i = 0; i <detectedObjects.size();i++) {
                    Integer id = detectedObjects.get(i).getTrackingId();
                    Rect bounds = detectedObjects.get(i).getBoundingBox();
                    canvas.drawRect(bounds,p);
                    switch(detectedObjects.get(i).getClassificationCategory()){
                        case 0: canvas.drawText("UNKNOWN" + detectedObjects.size(), bounds.right,bounds.bottom,p);break;
                        case 1: canvas.drawText("HOME_GOOD", bounds.right,bounds.bottom,p);break;
                        case 2: canvas.drawText("FASHION_GOOD", bounds.right,bounds.bottom,p);break;
                        case 3: canvas.drawText("FOOD", bounds.right,bounds.bottom,p);break;
                        case 4: canvas.drawText("PLACE", bounds.right,bounds.bottom,p);break;
                        case 5: canvas.drawText("PLANT", bounds.right,bounds.bottom,p);break;
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
