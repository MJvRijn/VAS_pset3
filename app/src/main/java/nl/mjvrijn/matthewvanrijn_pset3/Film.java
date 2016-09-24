//http://www.parcelabler.com/
package nl.mjvrijn.matthewvanrijn_pset3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class Film implements Serializable{
    private File poster;
    private String title;
    private String imdbID;
    private int year;

    public Film(File cacheDir, JSONObject json) {
        try {
            title = json.getString("Title");
            year = json.getInt("Year");
            imdbID = json.getString("imdbID");

            try {
                URL poster_url = new URL(json.getString("Poster"));
                setPoster(cacheDir, BitmapFactory.decodeStream(poster_url.openStream()));
            } catch(Exception e) {
                poster = null;
                Log.i("Networking", "Failed to get poster for \"" + title + "\"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getPoster() {
        if(poster != null) {
            return BitmapFactory.decodeFile(poster.getAbsolutePath());
        } else {
            return null;
        }
    }

    public void setPoster(File cacheDir, Bitmap p) {
        File f = null;
        FileOutputStream fos = null;
        try {
            f = new File(cacheDir, p.hashCode() + ".png");
            fos = new FileOutputStream(f);
        } catch(IOException e) {
            e.printStackTrace();
        }

        p.compress(Bitmap.CompressFormat.PNG, 75, fos);

        poster = f;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int y) {
        year = y;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String i) {
        imdbID = i;
    }
}
