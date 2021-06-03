package com.example.irhythm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();

    }
    boolean shuffleBoolean = false;
    boolean repeatBoolean = false;
    int seekForwardTime = 5000;
    int seekBackwardTime = 5000;
    TextView textView;
    ImageView play , previous, next, replay, shuffle;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent ;
    int position;
    int positionTemp;
    SeekBar seekBar;
    Thread updateSeek ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar= findViewById(R.id.seekBar);
        replay = findViewById(R.id.replay);
        shuffle = findViewById(R.id.shuffle);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent=intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position= intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try{
                    if(repeatBoolean){
                        position = positionTemp;
                    }
                    else if(shuffleBoolean){
                        position = getRandom(songs.size() - 1);
                        System.out.println("Printing position from next"+position);
                    }
                    else if(position!=songs.size()-1){
                        position = position + 1;
                    }
                    else{
                        position = 0;
                    }
                    Uri uri = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                    seekBar.setMax(mediaPlayer.getDuration());
                    textContent = songs.get(position).getName().toString();
                    textView.setText(textContent);
                    updateSeek.start();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run(){
                int currentPosition =0;
              try {
                  while(currentPosition<mediaPlayer.getDuration()){
                      currentPosition = mediaPlayer.getCurrentPosition();
                      seekBar.setProgress(currentPosition);
                      sleep(1000);
                  }
              }
              catch (Exception e ){
                  e.printStackTrace();
//                  if(e instanceof IllegalStateException){
//                      for(int i = 0; i < 2; i++){
//                                  currentPosition = mediaPlayer.getCurrentPosition();
//                                  seekBar.setProgress(currentPosition);
//                          }
//                      }
                  }
              }
        };
        System.out.println("I am here");
        updateSeek.start();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                positionTemp = position;
//                mediaPlayer.stop();
//                mediaPlayer.release();
                if(repeatBoolean){
                    position = positionTemp;
                }
                else if(shuffleBoolean){
                    position = getRandom(songs.size() - 1);
                    System.out.println("Printing position from previous"+position);
                }
                else if(position!=0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });
        previous.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if(currentPosition - seekBackwardTime >=0){
                    mediaPlayer.seekTo(currentPosition-seekBackwardTime);
                }
                else{
                    mediaPlayer.seekTo(0);
                }
                return true;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
//                mediaPlayer.stop();
//                mediaPlayer.release();
                if(repeatBoolean){
                    position = positionTemp;
                }
                else if(shuffleBoolean){
                    position = getRandom(songs.size() - 1);
                    System.out.println("Printing position from next"+position);
                }
                else if(position!=songs.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });
        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();

                if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
                    mediaPlayer.seekTo(currentPosition+seekForwardTime);
                }
                else{
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
                return true;
            }
        });
        replay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    //replay.setLayoutParams(params);
                    repeatBoolean = false;
                    replay.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
                else{
                    replay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    repeatBoolean = true;
                }
                System.out.println(repeatBoolean);
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(shuffleBoolean){
                    shuffle.setBackgroundColor(getResources().getColor(android.R.color.white));
                    shuffleBoolean = false;
                }
                else{
                    shuffle.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    shuffleBoolean = true;
                }
                System.out.println(shuffleBoolean);
            }
        });
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }
}