package van.srt;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 23/10/2016.
 */
public class SrtRecord {
    private final SimpleFloatProperty id;
    private final SimpleStringProperty time;
    private final SimpleStringProperty sub;

    public SrtRecord() {
        this(0, "", "");
    }

    public SrtRecord(float id, String time, String sub) {
        this.id = new SimpleFloatProperty(id);
        this.time = new SimpleStringProperty(time);
        this.sub = new SimpleStringProperty(sub);
    }

    public float getId() {
        return id.get();
    }

    public SimpleFloatProperty idProperty() {
        return id;
    }

    public void setId(float id) {
        this.id.set(id);
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

    @Override
    public String toString() {
        return id+"|"+time+"|"+sub;
    }
}
