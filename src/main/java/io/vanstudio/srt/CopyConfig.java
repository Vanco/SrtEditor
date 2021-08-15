package io.vanstudio.srt;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 29/10/2016.
 */
public class CopyConfig {
    enum Inset {
        BEFORE, AFTER;
    }

    private final SimpleFloatProperty targetId;
    private final SimpleObjectProperty<Inset> inset;
    private final SimpleFloatProperty originalId;
    private final SimpleObjectProperty<SrtTime> srtTime;
    private final SimpleStringProperty sub;
    private final SimpleLongProperty intervalBefore;

    public CopyConfig(float target, Inset inset, SrtRecord srtRecord, long intervalBefore) {
        this.targetId = new SimpleFloatProperty(target);
        this.originalId = new SimpleFloatProperty(srtRecord.getId());
        this.inset = new SimpleObjectProperty<>(inset);
        this.srtTime = new SimpleObjectProperty<>(new SrtTime(srtRecord.getTime()));
        this.sub = new SimpleStringProperty(srtRecord.getSub());
        this.intervalBefore = new SimpleLongProperty(intervalBefore);
    }

    public float getTargetId() {
        return targetId.get();
    }

    public SimpleFloatProperty targetIdProperty() {
        return targetId;
    }

    public void setTargetId(float targetId) {
        this.targetId.set(targetId);
    }

    public Inset getInset() {
        return inset.get();
    }

    public SimpleObjectProperty<Inset> insetProperty() {
        return inset;
    }

    public void setInset(Inset inset) {
        this.inset.set(inset);
    }

    public float getOriginalId() {
        return originalId.get();
    }

    public SimpleFloatProperty originalIdProperty() {
        return originalId;
    }

    public void setOriginalId(float originalId) {
        this.originalId.set(originalId);
    }

    public SrtTime getSrtTime() {
        return srtTime.get();
    }

    public SimpleObjectProperty<SrtTime> srtTimeProperty() {
        return srtTime;
    }

    public void setSrtTime(SrtTime srtTime) {
        this.srtTime.set(srtTime);
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

    public long getIntervalBefore() {
        return intervalBefore.get();
    }

    public SimpleLongProperty intervalBeforeProperty() {
        return intervalBefore;
    }

    public void setIntervalBefore(long intervalBefore) {
        this.intervalBefore.set(intervalBefore);
    }
}
