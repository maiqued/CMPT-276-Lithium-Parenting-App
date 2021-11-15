package ca.cmpt276.parentapp.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child;
import ca.cmpt276.parentapp.model.ChildManager;

/**
 * ChildAdd class:
 *
 * UI class for adding a child in the configure child activity
 */
public class ChildAdd extends AppCompatActivity {

    EditText editTextChildAdd;
    ImageView imageView;
    private ChildManager children;
    private String directoryPath;
    Button btnOpen;
    Button btnGallery;
    private static final int IMAGE_FROM_GALLERY = 1000;
    String storagePermission[];
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_add);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.add_child_title);
        ab.setDisplayHomeAsUpEnabled(true);
        editTextChildAdd = findViewById(R.id.editTextChildAdd);
        storagePermission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        setupAdd();
        setupTakePhoto();
        setupAddPhotoFromGallery();

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ChildAdd.class);
    }


    //https://www.youtube.com/watch?v=qO3FFuBrT2E
    private void setupTakePhoto() {

        imageView = findViewById(R.id.childPhoto);
        btnOpen = findViewById(R.id.takePhotobtn);
        if(ContextCompat.checkSelfPermission(ChildAdd.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ChildAdd.this,
                    new String[]{
                        Manifest.permission.CAMERA
                    },
                    100);
        }
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                }


            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!= null){
                    activityResultLauncher.launch(intent);
                }
            }
        });

    }

    //setup gallery selection issue.

    private void setupAddPhotoFromGallery() {

        imageView = findViewById(R.id.childPhoto);
        btnGallery = findViewById(R.id.chooseFromGallerybtn);

        if(ContextCompat.checkSelfPermission(ChildAdd.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ChildAdd.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    IMAGE_FROM_GALLERY);
        }
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()== RESULT_OK && result.getData() != null){
                            Bundle bundle = result.getData().getExtras();
                            Bitmap bitmap = (Bitmap) bundle.get("data");
                            imageView.setImageBitmap(bitmap);
                        }

                    }
                });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                if(intent.resolveActivity(getPackageManager())!= null){
                    activityResultLauncher.launch(intent);
                }
            }
        });

    }



    private void setupAdd() {
        Button save = (Button) findViewById(R.id.btnAddChild);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = editTextChildAdd.getText().toString();
                if(name.matches("")) {
                    String message = getString(R.string.warning_name_empty);
                    Toast.makeText(ChildAdd.this, message, Toast.LENGTH_SHORT).show();
                }

                else {


                    //https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-
                    // from-internal-memory-in-android
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmapImage = drawable.getBitmap();
                    ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

                    File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);

                    File mPath= new File(directory, editTextChildAdd.getText().toString() + ".jpg");

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mPath);
                        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    }
                    catch(Exception exception) {
                        exception.printStackTrace();
                    }
                    finally {
                        try {
                            fos.close();
                        }
                        catch(IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    directoryPath = directory.getAbsolutePath();

                    Child child = new Child(name);
                    ChildManager.getInstance().addChild(child);
                    String message = name + getString(R.string.x_added);
                    Toast.makeText(ChildAdd.this, message, Toast.LENGTH_SHORT).show();
                    children.setPath(directoryPath);
                    finish();
                }


            }
        });
    }
}