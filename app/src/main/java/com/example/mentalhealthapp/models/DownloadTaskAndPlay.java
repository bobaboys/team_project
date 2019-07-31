package com.example.mentalhealthapp.models;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
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
    private  MediaPlayer player;

    public DownloadTaskAndPlay(Context context, ImageView btnPlay, String downloadUrl, String pathDownload, String dFileName, MediaPlayer player) {
        this.context = context;
        this.pathDownload=pathDownload;
        this.playButton = btnPlay;
        this.downloadUrl = downloadUrl;
        this.player = player;

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
            //TODO CHANGE COLOR IMAGEVIEW PLAY BTN. BLOCK MAYBE?
            Utils.enableDisablePlay(context,playButton, false);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                Utils.enableDisablePlay(context,playButton, true);
                if (outputFile != null) {
                    play(outputFile.getAbsolutePath());
                    //buttonText.setText(R.string.downloadCompleted);//If Download completed then change button text
                } else {
                    Log.e(TAG,"Error downloading audio");

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

/**************
                //Get File if SD card is present
                if (Utils.isSDCardPresent()) {

                    apkStorage = new File(
                            pathDownload);
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
 *************/
                apkStorage = new File(
                        pathDownload);//******************* no estamos usando la SD

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
    public void play( String SOMETHINGCANTREMEMBERWHAT) throws IOException {//TODO GET NO SE QUE GET HAHAHA
        player = new MediaPlayer();
        player.setDataSource(SOMETHINGCANTREMEMBERWHAT);
        player.prepare();
        player.start();
        player.setOnCompletionListener(completionListener);
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //  TODO
            Utils.enableDisablePlay(context,playButton, true);
        }
    };
}