package com.example.mymediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles)
    {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
    holder.file_name.setText(mFiles.get(position).getTitle());
    byte[] image = getAlbumArt(mFiles.get(position).getPath());
    if (image != null) {
        Glide.with(mContext).asBitmap()
                .load(image)
                .into(holder.album_art);

    } else {
        Glide.with(mContext).asBitmap()
                .load(R.drawable.mymusicplayer)
                .into(holder.album_art);
    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PlayerActivity.class);
            intent.putExtra("position" , position);
            mContext.startActivity(intent);
        }
    });
    holder.menu_more.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(mContext , v);
            popupMenu.getMenuInflater().inflate(R.menu.more_menu , popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            Toast.makeText(mContext , "Deleted" , Toast.LENGTH_SHORT).show();
                            deleteFile(position , v);
                            break;
                    }
                    return true;
                }
            });
        }
    });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private void deleteFile(int position , View v) {
        mFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position , mFiles.size());
        Snackbar.make(v , "Deleted" , Snackbar.LENGTH_SHORT).show();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView file_name ;
        ImageView album_art , menu_more ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu_more = itemView.findViewById(R.id.menu_more);
        }
    }

    private  byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
