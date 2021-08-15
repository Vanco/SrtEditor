package io.vanstudio.srt;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 25/10/2016.
 */
public class SrtWriter extends BufferedWriter {
    public SrtWriter(Writer out) {
        super(out);
    }

    public void write(SrtRecord srtRecord) throws IOException {
        write(toStr(srtRecord.getId()));
        newLine();
        write(srtRecord.getTime());
        newLine();
        write(srtRecord.getSub());
        if (srtRecord.getSub().endsWith("\n")) {
            newLine();
        } else {
            newLine();
            newLine();
        }
    }

    private String toStr(float id) {
        return ""+ (int) id;
    }
}
