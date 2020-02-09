package xyz.vedant.customcamera.data;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import xyz.vedant.customcamera.adapter.PicGridAdapter;
import xyz.vedant.customcamera.model.PicGridModel;

public class PicGridData {
    Context context;
    public static ArrayList<PicGridModel> picGridModels = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;

    public PicGridData(final Context context, final RecyclerView recyclerView, final ProgressBar progressBar) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.context = context;
        String root = Environment.getExternalStorageDirectory().toString();
        final File myDir = new File(root + "/req_images");
        //clear the list
        picGridModels.clear();
        final HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.clear();
        FirebaseDatabase.getInstance().getReference().child("track_download")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //set the mpa
                        picGridModels.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String curr_filename = ds.getKey();
                            hashMap.put(curr_filename, true);
                        }

                        if (myDir.isDirectory() && Objects.requireNonNull(myDir.listFiles()).length > 0) {
                            for (File image_file : myDir.listFiles()) {
                                String curr_filename = image_file.getName();
                                String curr_filepath = image_file.getAbsolutePath();
                                boolean b = false;
                                String curr_filename1 = curr_filename;
                                curr_filename1 = curr_filename1.replace(".jpg", "");
                                curr_filename1 = curr_filename1.replace(".png", "");
                                if (hashMap.containsKey(curr_filename1)) {
                                    b = true;
                                }
                                PicGridModel picGridModel = new PicGridModel(curr_filename, curr_filepath, b);
                                //appending to the end
                                picGridModels.add(0, picGridModel);
                            }
                        }

                        if (picGridModels.size() > 0) {
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            PicGridAdapter picGridAdapter = new PicGridAdapter(context, picGridModels);
                            recyclerView.setAdapter(picGridAdapter);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, "No images available", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
