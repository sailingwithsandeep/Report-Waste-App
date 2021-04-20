package com.example.dirtytoilet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TakePicture extends AppCompatActivity {

    private static final String UPLOAD_URL = "https://www.rkuinfo.ml/hackathon/main/services/uploadimage.php";
    //private static final String UPLOAD_URL = "https://10.90.0.33/uploadimagedemo/uploadimage.php";
    ImageView image;

    Button upload;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    private Bitmap bitmap;
    Boolean status = false;
    String path;
    TextView infotext, otp;
    int randomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);


        image = findViewById(R.id.image);
        upload = findViewById(R.id.upload);
        infotext = findViewById(R.id.infotext);
        otp = findViewById(R.id.otp);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionManager.checkStoragePermission(TakePicture.this)) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(TakePicture.this);
                }
            }
        });
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) TakePicture.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(otp.getText());
                Toast.makeText(TakePicture.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.startAnimation(buttonClick);
                if (status) {
                    uploadMultipart();
                    startActivity(new Intent(TakePicture.this, MainActivity.class));
                } else
                    Toast.makeText(TakePicture.this, "Please Capture Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filePath = result.getUri();
            try {

                infotext.setVisibility(View.VISIBLE);
                Random rn = new Random();
                int range = (999999 - 1000) + 1;
                randomNum = rn.nextInt(range) + 1000;
                otp.setText(Integer.toString(randomNum));
                otp.setVisibility(View.VISIBLE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                status = true;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadMultipart() {
        //getting name for the image

        Log.d("Filepath===>", String.valueOf(filePath));
        //getting the actual path of the image
        path = getPath(filePath);
        Log.d("IMage Path===>", path);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", "name")
                    .addParameter("otp",Integer.toString(randomNum) )
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();

        } catch (Exception exc) {
            Toast.makeText(this, "Error==" + exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
            status = true;
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static double getRandomNumber() {
        double x = Math.random();
        return x;
    }
}
