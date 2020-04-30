package com.github.pawelrozniecki.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class TextDetector {

    Context mContext;

    public TextDetector(Context mContext){
        this.mContext = mContext;
    }


    protected void processText(Bitmap bitmap, final GridLayout grid, final GraphicOverlay overlay) {



        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().
                getOnDeviceTextRecognizer();

        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                grid.removeAllViews();
                String result = firebaseVisionText.getText();
                List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();

                EditText editText = new EditText(mContext.getApplicationContext());
                editText.setText(result);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                editText.setTextSize(18);
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                editText.setTextColor(mContext.getResources().getColor(R.color.darkIcons));
                editText.setScroller(new Scroller(mContext));
                editText.setMaxLines(10);
                editText.setTextIsSelectable(true);

                editText.setVerticalScrollBarEnabled(true);
                editText.setMovementMethod(new ScrollingMovementMethod());
                grid.addView(editText);

                overlay.clear();
                for (int i = 0; i < blocks.size(); i++) {
                    List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                    for (int j = 0; j < lines.size(); j++) {
                        List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                        for (int k = 0; k < elements.size(); k++) {
                            GraphicOverlay.Graphic textGraphic = new TextGraphic(overlay, elements.get(k));
                            overlay.add(textGraphic);

                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                grid.removeAllViews();
                TextView textView = new TextView(mContext.getApplicationContext());
                textView.setText("No text detected");
                grid.addView(textView);

            }
        });


    }



}
