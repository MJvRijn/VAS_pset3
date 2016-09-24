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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Films extends AppCompatActivity {
    private ArrayList<Film> favourites;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Watch List");
        favourites = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.films_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmsAdapter(favourites);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeListener = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int swipeDir) {
                final int pos = vh.getAdapterPosition();
                final Film toRemove = favourites.get(pos);

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

                //Remove swiped item from list and notify the RecyclerView
            }
        };

        ItemTouchHelper swipeHelper = new ItemTouchHelper(swipeListener);

        swipeHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateData();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_films, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_add) {
            Intent i = new Intent(Films.this, Add.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Films.this);
        Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());
        ArrayList<Film> toRemove = new ArrayList<>();
        ArrayList<String> imdbIDs = new ArrayList<>();

        // Removed from dataset but not from preferences
        for(Film f : favourites) {
            if(!saved.contains(f.getImdbID())) {
                toRemove.add(f);
            } else {
                imdbIDs.add(f.getImdbID());
            }
        }

        // Removed from preferences but no from dataset
        for(String imdbID : saved) {
            if(!imdbIDs.contains(imdbID)) {
                new ImdbIDTask().execute(imdbID);
            }
        }

        favourites.removeAll(toRemove);
        adapter.notifyDataSetChanged();
    }

    private class ImdbIDTask extends AsyncTask<String, Integer, String> {
        private Film result;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(String.format("http://www.omdbapi.com/?i=%s", params[0]));
                InputStream is = url.openStream();

                // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String json = s.hasNext() ? s.next() : "";

                JSONObject reader = new JSONObject(json);
                result = new Film(getCacheDir(), reader);

            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            favourites.add(result);
            adapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }
}
