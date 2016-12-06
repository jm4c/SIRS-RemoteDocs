package utils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class MiscUtils {

    //needed for GUI
    public static String[] getStringArrayFromCollection(Collection<String> collection){
        return collection.toArray(new String[collection.size()]);
    }

    public static String getDateFormatted(Date date){
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");

        return ft.format(date);
    }

}
