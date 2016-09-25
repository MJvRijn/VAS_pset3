package nl.mjvrijn.matthewvanrijn_pset3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Films extends AppCompatActivity {
    private ArrayList<Film> favourites = new ArrayList<>();
    private RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Watch List");

        // Set up RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.films_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmsAdapter(favourites);
        recyclerView.setAdapter(adapter);

        /** Set up Swipe-to-Delete **/

        /* Set up the listener that is called when an item in the recyclerview is moved or swiped off
         * screen. */
        ItemTouchHelper.SimpleCallback swipeListener = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            /* onMove is called when the item is moved, in future this is where feedback like a
             * background colour change could be implemented. */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                return false;
            }

            /* OnSwiped is called when the item is completely swiped off screen, it handles the
             * the removal from the dataset and the confirmation. */
            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int swipeDir) {
                // Get the Film object related to the RecyclerView item that was swiped
                final int pos = vh.getAdapterPosition();
                final Film toRemove = favourites.get(pos);

                // Create and show a confirmation dialog box, in which pressing yes removes the film
                // from the dataset and the SharedPreferences and pressing no notifies the recyclerview
                // that the item must be put back.
                new AlertDialog.Builder(Films.this)
                    .setTitle("Remove from watch list")
                    .setMessage(String.format("Are you sure you want to remove \"%s\" from your watch list?"
                                                                            , toRemove.getTitle()))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            favourites.remove(toRemove);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Films.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());
                            saved.remove(toRemove.getImdbID());
                            editor.apply();
                            adapter.notifyItemRemoved(pos);
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(pos);
                        }

                    })
                    .show();
            }
        };

        // Attach the listener via an ItemTouchHelper (for animations) to the RecyclerView
        new ItemTouchHelper(swipeListener).attachToRecyclerView(recyclerView);
    }

    /* When the app is started or the watch list is resumed the data set and SharedPreferences
     * must be re-synchronised. */
    @Override
    protected void onResume() {
        super.onResume();

        updateData();
    }

    /* Film posters are too large to be passed between activities by Intent, so they are saved to
     * cache. When the app is closed this cache is cleared of all .png files. */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("Files", "Cleaning Cache");
        File posterDir = new File(getCacheDir() + "posters/");

        if(posterDir.isDirectory()) {
            String[] posters = posterDir.list();

            for(String poster : posters) {
                if(poster.contains(".png")) {
                    new File(posterDir, poster).delete();
                }
            }
        }
    }

    /* Add the "add" button to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_films, menu);
        return true;
    }

    /* Go to the Add activity when the add button is pressed. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add) {
            Intent i = new Intent(Films.this, Add.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /* Synchronise the data set with stored preferences. Do this by first removing all films from
     * the data set that are not stored in the preferences and then adding all films from the
     * preferences that are not in the data set. */
    private void updateData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Films.this);
        Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());
        ArrayList<Film> toRemove = new ArrayList<>();
        ArrayList<String> imdbIDs = new ArrayList<>();

        // Removed from data set but not from preferences
        for(Film f : favourites) {
            if(!saved.contains(f.getImdbID())) {
                toRemove.add(f);
            } else {
                imdbIDs.add(f.getImdbID());
            }
        }

        // Removed from preferences but no from data set
        for(String imdbID : saved) {
            if(!imdbIDs.contains(imdbID)) {
                new ImdbIDTask().execute(imdbID);
            }
        }

        favourites.removeAll(toRemove);
        adapter.notifyDataSetChanged();
    }

    /* Definition of an asynchronous task to create a Film object from an imdbID, using the OMDB api */
    private class ImdbIDTask extends AsyncTask<String, Integer, String> {
        private Film result;

        /* Get the data and create the Film asynchronously. */
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(String.format("http://www.omdbapi.com/?i=%s", params[0]));
                JSONObject json = new JSONObject(Utils.inputSteamToString(url.openStream()));
                result = new Film(getCacheDir(), json);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /* Then update the data set and notify the adapter. */
        @Override
        protected void onPostExecute(String s) {
            favourites.add(result);
            adapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }
}
