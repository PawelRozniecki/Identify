package com.github.pawelrozniecki.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LabelDetector {


    private Context mContext;
    private TextView badge;

    public LabelDetector(Context mContext){

        this.mContext = mContext;

    }

    protected void detectLabels(Bitmap bitmap, final GridLayout grid) throws IOException {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        final FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();


        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(final List<FirebaseVisionImageLabel> l) {
                grid.removeAllViews();



                for (int i = 0; i < l.size(); i++) {

                    badge = new TextView(mContext.getApplicationContext());
                    customizeBadge(badge);
                    badge.setText(l.get(i).getText());
                    grid.addView(badge);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error", e.toString());
            }
        });

    }

    public void customizeBadge(TextView badge) {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(300, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(10, 20, 10, 15);
        badge.setLayoutParams(lp);
        badge.setBackgroundResource(R.drawable.roundedhape);
        badge.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        badge.setPadding(20, 20, 20, 20);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(mContext.getResources().getColor(R.color.white));
        badge.setLayoutParams(lp);
    }

}
