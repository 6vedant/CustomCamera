package xyz.vedant.customcamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import xyz.vedant.customcamera.data.PicGridData;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tv_upload_status;
    public HashMap<String, Boolean> map;

    public HashMap<String, Boolean> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Boolean> map) {
        this.map = map;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        try {
            getSupportActionBar().setTitle("Gallery");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycleview_grid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_upload_status = (TextView) findViewById(R.id.textview_sync_status);

        try {
            PicGridData picGridData = new PicGridData(GalleryActivity.this, recyclerView, progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.sync_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_upload_status.setText("Uploading...");
                startUploading();
            }
        });


    }

    public void startUploading() {
        final HashMap<String, Boolean> hashMap = new HashMap<>();
        FirebaseDatabase.getInstance().getReference().child("track_download")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String curr_key = ds.getKey();
                                String curr_val = (String) ds.getValue();
                                hashMap.put(curr_key, true);
                            }

                            setMap(hashMap);
                            startImageUploading(hashMap);


                        } else {
                            startImageUploading(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void startImageUploading(HashMap<String, Boolean> hashMap) {
        UploadFile uploadFile = new UploadFile(GalleryActivity.this, tv_upload_status);
        uploadFile.execute();

    }

    class UploadFile extends AsyncTask<Void, Long, Boolean> {
        private static final int MEGABYTE = 1024 * 1024;
        ProgressDialog progressDialog;
        Context context;
        TextView tv_status;
        public ArrayList<String> file_names;

        public ArrayList<String> getFile_names() {
            return file_names;
        }

        public void setFile_names(ArrayList<String> file_names) {
            this.file_names = file_names;
        }

        public ArrayList<String> pending_files;

        public ArrayList<String> getPending_files() {
            return pending_files;
        }

        public void setPending_files(ArrayList<String> pending_files) {
            this.pending_files = pending_files;
        }

        StorageReference storageReference;

        public UploadFile(Context context, TextView tv_status) {
            //initiliase the upload
            //fetch the files to be uploaded after sync and refresh
            this.context = context;
            this.tv_status = tv_status;

            storageReference = FirebaseStorage.getInstance().getReference();
            ArrayList<String> list_local = new ArrayList<>();
            ArrayList<String> list_names = new ArrayList<>();
            list_names.clear();
            list_local.clear();
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/req_images");
            //clear the list
            for (File image_file : myDir.listFiles()) {
                String curr_filename = image_file.getName();
                String curr_filepath = image_file.getAbsolutePath();
                curr_filename = curr_filename.replace(".jpg", "");
                curr_filename = curr_filename.replace(".png", "");

                //already downloaded or not
                if (getMap() != null) {
                    if (!getMap().containsKey(curr_filename)) {
                        list_local.add(curr_filepath);
                        list_names.add(curr_filename);
                    }
                } else {
                    list_local.add(curr_filepath);
                    list_names.add(curr_filename);
                }
            }
            Toast.makeText(context, "" + list_local.size() + " images found for uploading.", Toast.LENGTH_SHORT).show();

            setPending_files(list_local);
            setFile_names(list_names);

            if (getPending_files().size() == 1) {
                uploadOne(getPending_files().get(0), getFile_names().get(0));
            }
        }

        public void uploadOne(String file_path, String file_name) {
            File curr_file = new File(file_path);
            Uri curr_uri = Uri.fromFile(curr_file);
            StorageReference storageReference1 = storageReference.child("images/" + file_name + ".jpg");
            uploadImageFirebase(curr_uri, file_name, 1, storageReference1);
        }


        @Override
        protected Boolean doInBackground(Void... voids) {

            if (getPending_files().size() == 1) {
                return null;
            }

            if (getPending_files().size() > 0) {
                // start uploading one by one
                for (int i = 0; i < getPending_files().size(); i++) {
                    File curr_file = new File(getPending_files().get(i));
                    Uri curr_uri = Uri.fromFile(curr_file);
                    StorageReference storageReference1 = storageReference.child("images/" + getFile_names().get(i) + ".jpg");
                    uploadImageFirebase(curr_uri, getFile_names().get(i), i, storageReference1);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // after every thing uploaded
            if (getPending_files() != null) {
                tv_upload_status.setText("" + getPending_files().size() + " Uploaded.");
            } else {
                tv_upload_status.setText("All images are already uploaded.");
            }

        }

        public void uploadImageFirebase(Uri image_uri, final String file_name, final int count, StorageReference storageReference) {
            try {

                UploadTask uploadTask = storageReference.putFile(image_uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                tv_upload_status.setText("Uploaded " + count + "/" + getPending_files().size());
                                db = db.child("track_download");
                                db = db.child("" + file_name);
                                db.setValue("" + uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                tv_upload_status.setText("Uploaded " + count + "/" + getPending_files().size());
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }


}