package com.example.devarshpatel.audiorecorder;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    Button buttonstart,buttonstop,buttonplay,buttonpause;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName="recording";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonstart = findViewById(R.id.button1);
        buttonstop = findViewById(R.id.button2);
        buttonplay = findViewById(R.id.button3);
        buttonpause = findViewById(R.id.button4);
        buttonstop.setEnabled(false);
        buttonplay.setEnabled(false);
        buttonpause.setEnabled(false);
        random = new Random();
        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+CreateRandomAudioFileName(5)+"AudioRecording.3gp";
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    buttonstart.setEnabled(false);
                    buttonstop.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Recording Startes", Toast.LENGTH_LONG).show();
                }else {
                    requestPermission();
                }
            }
        });
        buttonstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                buttonstop.setEnabled(false);
                buttonplay.setEnabled(true);
                buttonstart.setEnabled(true);
                buttonpause.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
            }
        });
        buttonplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                buttonstop.setEnabled(false);
                buttonstart.setEnabled(false);
                buttonpause.setEnabled(true);
                mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                }catch (IOException e){
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(MainActivity.this,"Recording Playing",Toast.LENGTH_LONG).show();
            }
        });
        buttonpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonstop.setEnabled(false);
                buttonstart.setEnabled(true);
                buttonplay.setEnabled(true);
                buttonpause.setEnabled(false);
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i=0;
        while(i<string){
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO},RequestPermissionCode);
    }
    private void onRequestPermissionResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case RequestPermissionCode:
                if(grantResults.length > 0){
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(StoragePermission && RecordPermission){
                        Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                    }else {

                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission(){
            int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
