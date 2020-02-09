package xyz.vedant.customcamera.subactivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import uk.co.senab.photoview.PhotoViewAttacher;
import xyz.vedant.customcamera.R;
import xyz.vedant.customcamera.libs.glideLoader.GlideImageLoader;

public class FullImageActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String image_path = getIntent().getStringExtra("image_path");
        String image_title = getIntent().getStringExtra("image_name");
        setContentView(R.layout.activity_full_image);

        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("" + image_title);
        } catch (Exception e) {

        }

        imageView = (ImageView) findViewById(R.id.imag_full);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.app_logo)
                .priority(Priority.HIGH);

        new GlideImageLoader(imageView,
                progressBar).load(image_path, options);


        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(imageView);
        pAttacher.update();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
