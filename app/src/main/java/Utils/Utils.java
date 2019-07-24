package Utils;

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
}


