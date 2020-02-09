package xyz.vedant.customcamera.subactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.PictureResult;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import xyz.vedant.customcamera.CustomCamera;
import xyz.vedant.customcamera.R;


public class PicturePreviewActivity extends AppCompatActivity {

    private static PictureResult picture;
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static void setPictureResult(@Nullable PictureResult pictureResult) {
        picture = pictureResult;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        try {
            getSupportActionBar().setTitle("Confirm Pic");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBitmap(null);
        final PictureResult result = picture;
        if (result == null) {
            finish();
            return;
        }

        final ImageView imageView = findViewById(R.id.image);
        try {
            result.toBitmap(1000, 1000, new BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    setBitmap(bitmap);
                }
            });
        } catch (UnsupportedOperationException e) {
            imageView.setImageDrawable(new ColorDrawable(Color.GREEN));
            Toast.makeText(this, "Can't preview this format: " + picture.getFormat(),
                    Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.save_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    saveInFolder(bitmap);
                } else {
                    Toast.makeText(getApplicationContext(), "Image not ready", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.change_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            setPictureResult(null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void saveInFolder(Bitmap bm) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "" + timeStamp;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = pictureFile+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            startActivity(new Intent(getApplicationContext(), CustomCamera.class));
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unalbe to save image, Please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


}