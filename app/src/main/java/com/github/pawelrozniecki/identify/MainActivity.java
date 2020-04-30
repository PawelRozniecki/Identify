package com.github.pawelrozniecki.identify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAM_PERMISSION = 100;
    private static final int GALLERY_REQUEST = 200;
    private ImageView gallery, mainImage, camera;
    private BottomNavigationView bottomNavigationMenu;
    private boolean isText;
    private GridLayout grid;
    private LabelDetector labelDetector;
    private ObjectDetector objectDetector;
    private TextDetector textDetector;
    private Bitmap bitmap = null;
    private TextView title;
    Uri imageUri = null;
    GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        labelDetector = new LabelDetector(this);
        textDetector  = new TextDetector(this);
        objectDetector = new ObjectDetector(this);
        gallery = findViewById(R.id.gallery);
        bottomNavigationMenu = findViewById(R.id.bottom_navigation);


        mainImage = findViewById(R.id.mainImage);
        title = findViewById(R.id.title);
        title.setText(R.string.titleLabel);

//        label = findViewById(R.id.label);
        camera = findViewById(R.id.camera);
        grid = findViewById(R.id.grid);

        bottomNavigationMenu.setOnNavigationItemSelectedListener(navListener);
        grid.setPadding(50, 0, 50, 50);

        if (mainImage == null) {
            //TODO textView.setText("Image not loaded");
        }



        //Listeners
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //OK CODE 200
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAM_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_REQUEST) {

            imageUri = data.getData();
            setImageUri(imageUri);
            mainImage.setImageURI(imageUri);
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                setBitmap(bitmap);



                if (isText) {
                    textDetector.processText(getBitmap(),grid, mainImage);
                } else {
                    labelDetector.detectLabels(getBitmap(), grid);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CAMERA_REQUEST) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            setBitmap(bitmap);
            mainImage.setImageBitmap(getBitmap());
            try {
                if (isText) {
                    textDetector.processText(getBitmap(),grid,mainImage);
                } else {
                    labelDetector.detectLabels(getBitmap(), grid);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAM_PERMISSION);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.LabelDetect:
                            isText = false;
                            try {
                                labelDetector.detectLabels(getBitmap(), grid);
                                title.setText(R.string.titleLabel);

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Load an image first", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            break;

                        case R.id.textAnalyze:
                            isText = true;
                            try {
                                title.setText(R.string.titleText);
                                textDetector.processText(getBitmap(), grid, mainImage);


                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Load an image first", Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }
                            break;

                        case R.id.AnlyzeImage:

                            objectDetector.detectObjects(getBitmap(), mainImage);
                            title.setText(R.string.titleObject);
                            break;
                    }
                    return true;
                }
            };

//Getters and Settes
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImageUri(Uri imageUri){ this.imageUri = imageUri;}
    public  Uri getUri(){return imageUri;}
}

