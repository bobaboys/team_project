package com.example.mentalhealthapp.models;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Utils.Utils;


public class DownloadTaskAndPlay {

    private static final String TAG = "Download Task";
    private Context context;
    private ImageView playButton;
    private String downloadUrl = "", downloadFileName = "";
    private String pathDownload;
    private MediaPlayer player;
    private SeekBar seekBar;

    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(player.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };


    public DownloadTaskAndPlay(Context context, ImageView btnPlay, SeekBar seekBar, String downloadUrl, String pathDownload, String dFileName, MediaPlayer player) {
        this.context = context;
        this.pathDownload=pathDownload;
        this.playButton = btnPlay;
        this.downloadUrl = downloadUrl;
        this.player = player;
        this.seekBar = seekBar;

        downloadFileName = dFileName;//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }


    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.enableDisablePlay(context,playButton, false);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    play(outputFile.getPath());
                    //buttonText.setText(R.string.downloadCompleted);//If Download completed then change button text
                } else {
                    Log.e(TAG,"Error downloading audio");
                    Utils.enableDisablePlay(context,playButton, true);

                }

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(context,"Error downloading audio",Toast.LENGTH_LONG).show();
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }
                apkStorage = new File(
                        pathDownload);

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }


    public void play( String absFilePath) throws IOException {
        player = new MediaPlayer();
        player.setDataSource( absFilePath);
        player.prepare();
        seekBar.setMax(player.getDuration());
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        player.start();
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
        player.setOnCompletionListener(completionListener);
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Utils.enableDisablePlay(context,playButton, true);
            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            seekBar.setProgress(0);
        }
    };


    SeekBar.OnSeekBarChangeListener seekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
                player.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

}