package com.example.ezmr;

public class NotesBean {
    int length;
    String unicode;
    boolean isTone;
    double duration;
    double toneFreqInHz;

    public NotesBean(int length, String unicode, boolean isTone, double duration, double toneFreqInHz) {
        this.length = length;
        this.unicode = unicode;
        this.isTone = isTone;
        this.duration = duration;
        this.toneFreqInHz = toneFreqInHz;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public boolean isTone() {
        return isTone;
    }

    public void setTone(boolean tone) {
        isTone = tone;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getToneFreqInHz() {
        return toneFreqInHz;
    }

    public void setToneFreqInHz(double toneFreqInHz) {
        this.toneFreqInHz = toneFreqInHz;
    }

    @Override
    public String toString() {
        return "NotesBean{" +
                "length='" + length + '\'' +
                ", unicode='" + unicode + '\'' +
                ", isTone=" + isTone +
                ", duration=" + duration +
                ", toneFreqInHz=" + toneFreqInHz +
                '}';
    }
}
