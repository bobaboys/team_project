package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseException;
import com.parse.ParseFile;

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
}


