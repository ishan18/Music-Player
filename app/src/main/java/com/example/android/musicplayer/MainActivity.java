package com.example.android.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton load;
    final int FILE_LOAD_REQUEST=1;
    final int READ_STORAGE_PERMISSION=2;
    ImageButton playButton;
    Uri audioUri;
    int playing=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load=(ImageButton) findViewById(R.id.load_button);
        playButton=(ImageButton)findViewById(R.id.play);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioUri==null)
                    return;
                if(playing==0){
                    Intent serviceIntent=new Intent(MainActivity.this,NotificationService.class);
                    serviceIntent.setData(audioUri);
                    startService(serviceIntent);
                    playButton.setImageResource(R.drawable.pause);
                    playing=1;
                }else{
                    Intent serviceIntent=new Intent(MainActivity.this,NotificationService.class);
                    serviceIntent.setData(audioUri);
                    stopService(serviceIntent);
                    playButton.setImageResource(R.drawable.play);
                    playing=0;
                }
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,FILE_LOAD_REQUEST);
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_STORAGE_PERMISSION);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==READ_STORAGE_PERMISSION){
            if(grantResults.length>1 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_VIEW,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,FILE_LOAD_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==FILE_LOAD_REQUEST && resultCode==RESULT_OK){
            audioUri=data.getData();
        }
    }
}