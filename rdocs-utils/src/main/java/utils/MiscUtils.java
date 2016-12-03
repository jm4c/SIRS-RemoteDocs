package utils;

import java.util.Collection;

public class MiscUtils {

    //needed for GUI
    public static String[] getStringArrayFromCollection(Collection<String> collection){
        return collection.toArray(new String[collection.size()]);
    }

}
