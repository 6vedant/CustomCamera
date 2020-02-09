package xyz.vedant.customcamera.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import xyz.vedant.customcamera.R;
import xyz.vedant.customcamera.libs.glideLoader.GlideImageLoader;
import xyz.vedant.customcamera.model.PicGridModel;
import xyz.vedant.customcamera.subactivity.FullImageActivity;

public class PicGridAdapter extends RecyclerView.Adapter<PicGridAdapter.MyViewHolder> {

    Context context;
    public ArrayList<PicGridModel> picGridModels;

    public PicGridAdapter(Context context, ArrayList<PicGridModel> picGridModels) {
        this.context = context;
        this.picGridModels = picGridModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.app_logo)
                .priority(Priority.HIGH);

        new GlideImageLoader(holder.imageView,
                holder.progressBar).load(picGridModels.get(position).getPic_path(), options);

        //set timer
        String curr_image_name = picGridModels.get(position).getPic_name();
        //if it is a jpg
        curr_image_name = curr_image_name.replace(".jpg", "");
        //if it is a png
        curr_image_name = curr_image_name.replace(".png", "");

        try {
            holder.tv_timer.setText("" + curr_image_name.substring(8, 10) + ":" + curr_image_name.substring(10, 12));
        } catch (Exception e) {
            holder.tv_timer.setText("" + curr_image_name);
        }

        if (picGridModels.get(position).isUploaded()) {
            holder.iv_upload_status.setImageResource(R.drawable.ic_check_white_24);
        } else {
            holder.iv_upload_status.setImageResource(R.drawable.ic_file_upload_white_24dp);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click listener for image
                //open in fullimage
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("image_path", picGridModels.get(position).getPic_path());
                intent.putExtra("image_name", picGridModels.get(position).getPic_name());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return picGridModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView img_status;
        ProgressBar progressBar;
        TextView tv_timer;
        ImageView iv_upload_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img_item);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            tv_timer = (TextView) itemView.findViewById(R.id.timer_tv);
            iv_upload_status = (ImageView) itemView.findViewById(R.id.upload_status_iv);
        }
    }

}
