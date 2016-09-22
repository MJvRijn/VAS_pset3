package nl.mjvrijn.matthewvanrijn_pset3;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        setTitle("Films");
        favourites = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.films_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmsAdapter(favourites);
        loadSave();
        recyclerView.setAdapter(adapter);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSave() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor edit = prefs.edit();
        HashSet<String> tosave = new HashSet<>();
        tosave.add("tt0120815");
        tosave.add("tt0068646");
        tosave.add("tt0108052");
        tosave.add("tt1375666");
        tosave.add("tt0102926");
        tosave.add("tt0253474");
        tosave.add("tt0172495");
        tosave.add("tt0211915");
        tosave.add("tt0086190");
        edit.putStringSet("saved", tosave);
        edit.apply();

        Set<String> saved = prefs.getStringSet("saved", new HashSet<String>());

        for(String id : saved) {
            new APIConnection().execute(id);
        }
    }

    public class APIConnection extends AsyncTask<String, Integer, String> {
        private String address = "http://www.omdbapi.com/?i=%s";
        private Film result = new Film();

        @Override
        protected String doInBackground(String... params) {
            String imdbID = params[0];

            try {
                URL url = new URL(String.format(address, imdbID));
                InputStream is = url.openStream();

                // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String json = s.hasNext() ? s.next() : "";

                JSONObject reader = new JSONObject(json);

                result.setTitle(reader.getString("Title"));
                result.setYear(reader.getInt("Year"));
                URL poster_url = new URL(reader.getString("Poster"));
                result.setPoster(Drawable.createFromStream(poster_url.openStream(), null));

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
