package com.dqp.analysor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * <p>
 * todo:// others read func or write func
 */
public class FileUtilsPlus {


    /**
     * Read file by lines.
     * <p>
     * Consumer func you can implements
     *
     * @param fileName the file name
     * @param func     the func
     */
    public static void readFileByLines(String fileName, Consumer<String> func) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                func.accept(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


}
