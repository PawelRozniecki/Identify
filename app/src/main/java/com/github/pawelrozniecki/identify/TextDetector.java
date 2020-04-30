package com.github.pawelrozniecki.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
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


    protected void processText(final Bitmap bitmap, final GridLayout grid, final ImageView imageView) {



        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().
                getOnDeviceTextRecognizer();

        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.RED);
                p.setStrokeWidth(3);

                grid.removeAllViews();
                String result = firebaseVisionText.getText();
                List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
                EditText editText = new EditText(mContext.getApplicationContext());
                editText.setText(result);
                styleText(editText);
                grid.addView(editText);


                Bitmap bmp_Copy = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(bmp_Copy);

                for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                    String blockText = block.getText();
                    Float blockConfidence = block.getConfidence();
                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                    Rect blockFrame = block.getBoundingBox();
                    for (FirebaseVisionText.Line line: block.getLines()) {
                        String lineText = line.getText();
                        Float lineConfidence = line.getConfidence();
                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                        Rect lineFrame = line.getBoundingBox();
                        for (FirebaseVisionText.Element element: line.getElements()) {
                            String elementText = element.getText();
                            Float elementConfidence = element.getConfidence();
                            List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                            Rect elementFrame = element.getBoundingBox();
                            canvas.drawRect(elementFrame,p);


                        }
                    }
                }
            imageView.setImageBitmap(bmp_Copy);
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

    public void styleText(EditText editText){
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setTextSize(18);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setTextColor(mContext.getResources().getColor(R.color.darkIcons));
        editText.setScroller(new Scroller(mContext));
        editText.setMaxLines(10);
        editText.setTextIsSelectable(true);
        editText.setVerticalScrollBarEnabled(true);
        editText.setMovementMethod(new ScrollingMovementMethod());
    }


}
