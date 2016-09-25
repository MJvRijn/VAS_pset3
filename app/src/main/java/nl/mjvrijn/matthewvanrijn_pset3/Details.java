package nl.mjvrijn.matthewvanrijn_pset3;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Details extends AppCompatActivity {
    private Film film;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSaved();
            }
        });

        film = (Film) getIntent().getSerializableExtra("film");

        // If the film is midding details, get them. Else update the display with the details.
        if(film.getPlot() == null) {
            new GetMissingDetailsTask().execute();
        } else {
            updateDisplay();
        }
    }

    /* Find all Views and set them to the details from the Film */
    private void updateDisplay() {
        ImageView poster = (ImageView) findViewById(R.id.details_poster);
        TextView title = (TextView) findViewById(R.id.details_title);
        TextView year = (TextView) findViewById(R.id.details_year);
        TextView plot = (TextView) findViewById(R.id.details_plot);
        TextView runtime = (TextView) findViewById(R.id.details_runtime );
        TextView rating = (TextView) findViewById(R.id.details_rating);
        TextView director = (TextView) findViewById(R.id.details_director);

        title.setText(Html.fromHtml(String.format("<b>Title:</b> %s", film.getTitle())));
        year.setText(Html.fromHtml(String.format("<b>Year:</b> %d", film.getYear())));
        plot.setText(Html.fromHtml(String.format("<b>Plot:</b><br>%s", film.getPlot())));
        runtime.setText(Html.fromHtml(String.format("<b>Runtime:</b> %s", film.getRuntime())));
        director.setText(Html.fromHtml(String.format("<b>Director:</b> %s", film.getDirector())));
        rating.setText(Html.fromHtml(String.format("<b>Rating:</b> %.1f/10", film.getRating())));

        // If there is no poster, show the missing poster image instead.
        Bitmap bm = film.getPoster();
        if(bm != null) {
            poster.setImageBitmap(bm);
        } else {
            poster.setImageResource(R.drawable.no_poster);

            // zoom fit scaling is not suitable for missing image image, use centre fit instead.
            poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // Set the floating button to the right colour and icon.
        updateFAB();
    }

    /* Toggle whether this film is saved to the watch list by adding or removing it's ID from
     * the preferences. Show a Toats to provide the user with feedback. */
    private void toggleSaved() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Details.this);
        Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());
        SharedPreferences.Editor editor = prefs.edit();
        String imdbID = film.getImdbID();

        if(saved.contains(imdbID)) {
            saved.remove(imdbID);
            Toast.makeText(Details.this, "Removed from Watch List", Toast.LENGTH_SHORT).show();
        } else {
            saved.add(imdbID);
            Toast.makeText(Details.this, "Added to Watch List", Toast.LENGTH_SHORT).show();
        }

        editor.putStringSet("saved", saved);
        editor.apply();
        updateFAB();
    }

    /* Set the floating action bar to red with a minus icon if the film is on the watch list and
     * green with a plus icon if it is not. */
    private void updateFAB() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Details.this);
        Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());

        if(saved.contains(film.getImdbID())) {
            fab.setBackgroundTintList(ContextCompat.getColorStateList(Details.this, R.color.buttonSaved));
            fab.setImageResource(R.drawable.ic_remove_black_24dp);
        } else {
            fab.setBackgroundTintList(ContextCompat.getColorStateList(Details.this, R.color.buttonUnsaved));
            fab.setImageResource(R.drawable.ic_add_black_24dp);
        }
    }

    /* Definition of an asynchronous task to get missing details for this film, using the OMDB api */
    private class GetMissingDetailsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(String.format("http://www.omdbapi.com/?i=%s", film.getImdbID()));
                JSONObject json = new JSONObject(Utils.inputSteamToString(url.openStream()));
                film.updateDetails(json);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            updateDisplay();
            super.onPostExecute(s);
        }
    }
}
