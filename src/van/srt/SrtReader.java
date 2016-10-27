package van.srt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 23/10/2016.
 */
public class SrtReader extends BufferedReader {
    private static final int BOM = 0xFEFF;
    public SrtReader(Reader in) {
        super(in);
        detectAndRemoveBom();
    }

    private void detectAndRemoveBom() {
        if (markSupported()) {
            try {
                mark(3);
                int bom = read();
                if (bom != BOM) {
                    reset();
                }
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public SrtRecord readRecord() throws IOException {
        float id = readId();
        String time = readLine();
        String sub = readSub();
        return new SrtRecord(id, time, sub);
    }

    private String readSub() throws IOException {
        String s, result = "";
        for (;;) {
            s = readLine();
            if (s == null || s.isEmpty()) {
                break;
            }
            result += s + "\n";
        }
        return result;
    }

    private float readId() throws IOException {
        String s = readLine();
        try {
            return Float.valueOf(s);
        } catch (NumberFormatException e) {
            System.err.format("NumberFormatException: %s%n", e);
            // if detectAndRemoveBom fail, example, when open a (UTF-8 with BOM) srt file with wrong charset like GB2312
            // the NumberFormatException will thrown. most of time it is in first line of the file, so just return 1.
            return 1.0f;
        }
    }
}
