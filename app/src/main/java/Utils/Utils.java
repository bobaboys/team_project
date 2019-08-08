package Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
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


    public static void enableDisablePlay(Context context, ImageView btn, boolean enable){
        btn.setEnabled(enable);
        btn.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(
                    enable ? R.color.white : R.color.colorAccent)));
    }

    public static long milisFromDateSrt(String strDate){
        try {
            Date date1=new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
            return date1.getTime();
        }catch (Exception e) {
            return 0;
        }
    }

    public static void setProfileImage(ImageView imageView){
        ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        imageView.setImageBitmap(bm);
    }
}


