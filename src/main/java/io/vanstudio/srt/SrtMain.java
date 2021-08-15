package io.vanstudio.srt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 23/10/2016.
 */
public class SrtMain {
    public static void main(String[] args) {
        try {
            String fileName = "/Users/van/Desktop/NYMPHOMANIAC_EXTENDED_DIRECTORS_CUT_VOL_I.en.srt";
            SrtReader reader = new SrtReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            while (reader.ready()) {
                SrtRecord srtRecord = reader.readRecord();
                System.out.println(srtRecord);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

