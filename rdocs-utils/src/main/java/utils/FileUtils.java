package utils;

import java.io.*;

public class FileUtils {

    public static void storeFile(Object file, String path) throws IOException {
        FileOutputStream fout = new FileOutputStream(path);

        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(file);
        oos.close();

    }

    public static Object getFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream fin;
        fin = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fin);
        Object object = ois.readObject();
        ois.close();
        return object;
    }
}
