package com.example.kapritish.nytimessearch;

/**
 * Created by kapritish on 10/22/16.
 */
public class Headline {
    String main;
    String kicker;

    public String getMain() {
        return main;
    }

    public String getKicker() {
        return kicker;
    }

    public Headline(String main, String kicker) {
        this.main = main;
        this.kicker = kicker;
    }
}
