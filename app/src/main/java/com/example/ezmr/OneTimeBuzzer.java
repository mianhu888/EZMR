package com.example.ezmr;

public class OneTimeBuzzer extends TonePlayer{

    protected double duration = 0.5;

    public OneTimeBuzzer(double duration) {
        this.duration = duration;
    }

    public OneTimeBuzzer() {
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    protected void asyncPlayTrack() {
        playerWorker = new Thread(new Runnable() {
            public void run() {
                playTone(duration);
                stop();
            }
        });

        playerWorker.start();
    }
}
