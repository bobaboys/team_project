package Utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Utils {
    public static String getTimeStamp(long milis){
        Date d = new Date();
        long now = d.getTime();
        long deltaSec = (now - milis)/1000;
        if(deltaSec <60)return "" +deltaSec+" s";
        else if (deltaSec<3600) return "" +deltaSec/60+" m";
        else if (deltaSec<86400) return "" +deltaSec/3600+" h";
        else{
            Date past = new Date(milis);
            String pattern;
            pattern =past.getYear()==d.getYear() ?
                "MM-dd":
                "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(past);

        }
    }

    public static Bitmap convertFileToBitmap(ParseFile picFile){
        if(picFile == null){
            return null;
        }
        try {
            byte[] image = picFile.getData();
            if(image!=null){
                Bitmap pic = BitmapFactory.decodeByteArray(image, 0, image.length);
                return pic;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    public static void enableDisablePlay(Context context, ImageView btn, boolean enable){
        btn.setEnabled(enable);
        btn.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(
                    enable ? R.color.white : R.color.pastel_009)));
    }


}


