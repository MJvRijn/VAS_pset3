package nl.mjvrijn.matthewvanrijn_pset3;

import android.graphics.drawable.Drawable;

public class Film {
    private String title;
    private int year;
    private Drawable poster;

    public Film() {

    }

    public Drawable getPoster() {
        return poster;
    }

    public void setPoster(Drawable p) {
        poster = p;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int y) {
        year = y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }


}
