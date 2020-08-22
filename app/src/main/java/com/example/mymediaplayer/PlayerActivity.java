package com.example.mymediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import static com.example.mymediaplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.mymediaplayer.MainActivity.musicFiles;
import static com.example.mymediaplayer.MainActivity.repeatbool;
import static com.example.mymediaplayer.MainActivity.shufflebool;
import static com.example.mymediaplayer.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name , artist_name , duration_played , duration_total;
    ImageView image_Song , nextBtn , prevBtn , shuffleBtn , repeatBtn , backBtn;
    FloatingActionButton playPauseButton ;
    SeekBar seekBar;
    int position ;
    static ArrayList<MusicFiles> songsList = new ArrayList<>();
    Uri uri;
    static MediaPlayer mp;
    Handler handler = new Handler();
    private Thread playThread , prevThread , nextThread ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentData();

        song_name.setText(songsList.get(position).getTitle());
        artist_name.setText(songsList.get(position).getArtist());
        mp.setOnCompletionListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mp != null && fromUser) {
                    mp.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    int mCurrentpos = mp.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentpos);
                    duration_played.setText(formatTime(mCurrentpos));
                }
                handler.postDelayed(this , 1000) ;
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shufflebool)
                {
                    shufflebool = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shufflebool = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatbool)
                {
                    repeatbool = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatbool = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PlayerActivity.super.onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    private void nextThreadBtn() {

        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextButtonClicked() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            if (shufflebool && !repeatbool) {
                position = getRandom(songsList.size() - 1);
            } else if (!shufflebool && !repeatbool) {
                position = ((position+ 1) % songsList.size());
            }

            uri = Uri.parse(songsList.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song_name.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource((uri).toString());
            int durationtotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000 ;
            duration_total.setText(formatTime(durationtotal));
            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap;
            if (art!= null) {
                bitmap = BitmapFactory.decodeByteArray(art , 0 , art.length);
                ImgAnimation(this , image_Song , bitmap);

            } else {
                Glide.with(this).asBitmap()
                        .load(R.drawable.mymusicplayer)
                        .into(image_Song);
            }




            seekBar.setMax(mp.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });
            mp.setOnCompletionListener(this);

            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);

            mp.start();


        } else {
            mp.stop();
            mp.release();

            if (shufflebool && !repeatbool) {
                position = getRandom(songsList.size() - 1);
            } else if (!shufflebool && !repeatbool) {
                position += 1 % songsList.size();
            }


            uri = Uri.parse(songsList.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song_name.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource((uri).toString());
            int durationtotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000 ;
            duration_total.setText(formatTime(durationtotal));
            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap;
            if (art!= null) {
                bitmap = BitmapFactory.decodeByteArray(art , 0 , art.length);
                ImgAnimation(this , image_Song , bitmap);

            } else {
                Glide.with(this).asBitmap()
                        .load(R.drawable.mymusicplayer)
                        .into(image_Song);
            }

            seekBar.setMax(mp.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });
            mp.setOnCompletionListener(this);

            playPauseButton.setBackgroundResource(R.drawable.ic_play);
//            mp.start();
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void prevThreadBtn() {

        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevButtonClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevButtonClicked() {

        if (mp.isPlaying()) {
            mp.stop();
            mp.release();

            if (shufflebool && !repeatbool) {
                position = getRandom(songsList.size() - 1);
            } else if (!shufflebool && !repeatbool) {
                position = ((position - 1) < 0 ? songsList.size() - 1 : position - 1);
            }


            uri = Uri.parse(songsList.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song_name.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource((uri).toString());
            int durationtotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000 ;
            duration_total.setText(formatTime(durationtotal));
            byte[] art = retriever.getEmbeddedPicture();
            if (art!= null) {
                Glide.with(this).asBitmap()
                        .load(art)
                        .into(image_Song);
            } else {
                Glide.with(this).asBitmap()
                        .load(R.drawable.mymusicplayer)
                        .into(image_Song);
            }




            seekBar.setMax(mp.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });
            mp.setOnCompletionListener(this);

            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            mp.start();
        } else {
            mp.stop();
            mp.release();

            if (shufflebool && !repeatbool) {
                position = getRandom(songsList.size() - 1);
            } else if (!shufflebool && !repeatbool) {
                position = ((position - 1) < 0 ? songsList.size() - 1 : position - 1);
            }


            uri = Uri.parse(songsList.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song_name.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource((uri).toString());
            int durationtotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000 ;
            duration_total.setText(formatTime(durationtotal));
            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap;
            if (art!= null) {
                bitmap = BitmapFactory.decodeByteArray(art , 0 , art.length);
                ImgAnimation(this , image_Song , bitmap);
            } else {
                Glide.with(this).asBitmap()
                        .load(R.drawable.mymusicplayer)
                        .into(image_Song);
            }

            seekBar.setMax(mp.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });
            mp.setOnCompletionListener(this);

            playPauseButton.setBackgroundResource(R.drawable.ic_play);
//            mp.start();
        }
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }
        };
        playThread.start();

    }

    private String formatTime(int mCurrentpos) {
        String total0 = "" ;
        String total = "" ;
        String seconds = String.valueOf(mCurrentpos % 60);
        String minutes = String.valueOf(mCurrentpos / 60);
        total = minutes + ":" + seconds;
        total0 = minutes + ":0" + seconds;
        if (seconds.length() == 1) {
            return total0;
        } else {
            return total ;
        }
    }
    public void playPauseButtonClicked() {
        if (mp.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_play);
            mp.pause();
            seekBar.setMax(mp.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });


        } else {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            mp.start();
            seekBar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentpos = mp.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentpos);
                    }
                    handler.postDelayed(this , 1000) ;
                }
            });


        }
    }

    public void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        image_Song = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_previous);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        backBtn = findViewById(R.id.back_button);
        playPauseButton = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar);

    }
    public void getIntentData(){
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender!=null && sender.equals("albumDetails"))
        {
            songsList = albumFiles;
        }
        else {
            songsList = mFiles;
        }
        if (songsList != null) {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(songsList.get(position).getPath());
        }
        if (mp!=null) {
            mp.stop();
            mp.release();
        }
        mp = MediaPlayer.create(getApplicationContext() , uri);
        mp.start();

        seekBar.setMax(mp.getDuration() / 1000);
        metaData(uri);

    }
    public void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource((uri).toString());
        int durationtotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000 ;
        duration_total.setText(formatTime(durationtotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art!= null) {
            bitmap = BitmapFactory.decodeByteArray(art , 0 , art.length);
            ImgAnimation(this , image_Song , bitmap);

        } else {
            Glide.with(this).asBitmap()
                    .load(R.drawable.mymusicplayer)
                    .into(image_Song);
        }
    }
    public void ImgAnimation(final Context context , final ImageView imageView , final Bitmap bitmap) {
        Animation animout = AnimationUtils.loadAnimation(context , R.anim.fadeout);
        final Animation animin = AnimationUtils.loadAnimation(context , R.anim.fadein);
        animout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animout);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextButtonClicked();
        if (mp != null) {
            mp = MediaPlayer.create(getApplicationContext() , uri);
            mp.start();
            mp.setOnCompletionListener(this);
        }
    }
}
