package com.popseven.applock.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.popseven.applock.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private Context context;
    private ImageAdapterListener listener;
    private ArrayList<String> imageList = new ArrayList<>();

    public ImageAdapter(Context context, ArrayList<String> imageList, ImageAdapterListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {

        Glide.with(context).load(imageList.get(i))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageSelected(imageList.get(i));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onImageLongSelected(imageList.get(i));
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ImageAdapterListener {
        void onImageSelected(String image);
        void onImageLongSelected(String image);
    }

}