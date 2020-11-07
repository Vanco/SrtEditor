package van.srt;

import javafx.beans.property.SimpleLongProperty;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 29/10/2016.
 */
public class SrtTime {
    private final SimpleLongProperty start;
    private final SimpleLongProperty end;
    private final SimpleLongProperty duration;

    public SrtTime(long start, long duration) {
        this.start = new SimpleLongProperty(start);
        this.end = new SimpleLongProperty(start + duration);
        this.duration = new SimpleLongProperty(duration);
    }
    public SrtTime(String timeline) {
        String[] split = timeline.split(" --> ");
        start = new SimpleLongProperty(parse(split[0]));
        end = new SimpleLongProperty(parse(split[1]));
        duration = new SimpleLongProperty(end.get() - start.get());
    }

    public long getStart() {
        return start.get();
    }

    public SimpleLongProperty startProperty() {
        return start;
    }

    public void setStart(long start) {
        this.start.set(start);
    }

    public long getEnd() {
        return end.get();
    }

    public SimpleLongProperty endProperty() {
        return end;
    }

    public void setEnd(long end) {
        this.end.set(end);
    }

    public long getDuration() {
        return duration.get();
    }

    public SimpleLongProperty durationProperty() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration.set(duration);
    }

    public String getSrtTime() {
        return format(start.get()) + " --> " + format(end.get());
    }

    public static long parse (String time) {
        String[] split = time.split("[:,]");
        int hh = Integer.parseInt(split[0]);
        int mi = Integer.parseInt(split[1]);
        int ss = Integer.parseInt(split[2]);
        int ms = Integer.parseInt(split[3]);

        return (long) ((hh * 60 * 60 + mi * 60 + ss) * 1000 + ms);
    }

    public static String format( long time) {
        long ms = time % 1000;
        long t = time / 1000;
        long ss = t % 3600 % 60;
        long mi = t % 3600 / 60;
        long hh = t / 3600;
        return ""+append(hh,2)+":"+append(mi,2)+":"+append(ss,2)+","+append(ms,3);
    }

    private static String append(long l, int i) {
        String s = String.valueOf(l);
        while (s.length() < i) {
            s = "0"+s;
        }
        return s;
    }

    @Override
    public String toString() {
        return getSrtTime();
    }

    public void shift(long milliseconds) {
        this.start.set(start.get() + milliseconds);
        this.end.set(end.get() + milliseconds);
    }
}
