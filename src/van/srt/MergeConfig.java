package van.srt;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.nio.charset.Charset;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 25/10/2016.
 */
public class MergeConfig {
    private final SimpleStringProperty time;
    private final SimpleStringProperty sub;
    private final SimpleObjectProperty<Charset> charset;

    public MergeConfig(String time, String sub, Charset charset) {
        this.time = new SimpleStringProperty(time);
        this.sub = new SimpleStringProperty(sub);
        this.charset = new SimpleObjectProperty<>(charset);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getSub() {
        return sub.get();
    }

    public SimpleStringProperty subProperty() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub.set(sub);
    }

    public Charset getCharset() {
        return charset.get();
    }

    public SimpleObjectProperty<Charset> charsetProperty() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset.set(charset);
    }

    @Override
    public String toString() {
        return time.toString() + sub.toString() + charset;
    }
}
