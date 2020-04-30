package com.github.pawelrozniecki.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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



    Context mContext;

    public ObjectDetector(Context mContext) {
        this.mContext = mContext;
    }

    protected void detectObjects(final Bitmap bitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionObjectDetector detector = FirebaseVision.getInstance().getOnDeviceObjectDetector();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {

            @Override
            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {


                Paint p = new Paint();
                Canvas canvas = new Canvas(bitmap);
                for (FirebaseVisionObject obj : detectedObjects) {
                    Integer id = obj.getTrackingId();
                    Rect bounds = obj.getBoundingBox();
                    p.setColor(Color.RED);
                    canvas.drawRect(bounds,p);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
