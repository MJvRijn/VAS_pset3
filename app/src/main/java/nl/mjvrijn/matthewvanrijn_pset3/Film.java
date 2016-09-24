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
    private String imdbID;
    private String title;
    private String plot;
    private String runtime;
    private String director;
    private double rating;
    private int year;

    public Film(File cacheDir, JSONObject json) {
        try {
            // Add info available in both search and id queries
            title = json.getString("Title");
            year = json.getInt("Year");
            imdbID = json.getString("imdbID");

            // Add info exclusive to id queries
            if(json.has("Plot")) {
                plot = json.getString("Plot");
                director = json.getString("Director");
                rating = json.getDouble("imdbRating");
                runtime = json.getString("Runtime");
            }

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

    public String getImdbID() {
        return imdbID;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getPlot() {
        return plot;
    }

    public String getRuntime() {
        return runtime;
    }

    public double getRating() {
        return rating;
    }

    public int getYear() {
        return year;
    }
}
