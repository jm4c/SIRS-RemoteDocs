package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static void storeFile(Object file, String path) throws IOException {
        System.out.println("Storing file in " + path);

        //Find parent directory from path and create it if it doesn't exist
        File tmp = new File(path);
        new File(tmp.getParent()).mkdirs();

        FileOutputStream fout = new FileOutputStream(path);

        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(file);
        oos.close();

    }

    public static Object getFile(String path) throws IOException, ClassNotFoundException {
        System.out.println("Getting file from " + path);
        FileInputStream fin;
        fin = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fin);
        Object object = ois.readObject();
        ois.close();
        return object;
    }

    public static boolean fileExists(String path){
        return Files.exists(Paths.get(path));
    }

    public static boolean deleteFile(String path) throws IOException {
        return Files.deleteIfExists(Paths.get(path));
    }
}
