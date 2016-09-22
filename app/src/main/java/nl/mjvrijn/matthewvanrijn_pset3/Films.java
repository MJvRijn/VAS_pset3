package nl.mjvrijn.matthewvanrijn_pset3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

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
        new APIConnection().execute("tt4731008");
        new APIConnection().execute("tt0068646");
        new APIConnection().execute("tt0108052");
        new APIConnection().execute("tt1375666");
        new APIConnection().execute("tt0102926");
    }

    public class APIConnection extends AsyncTask<String, Integer, String> {
        private String address = "http://www.omdbapi.com/?i=%s";
        private Film result = new Film();

        @Override
        protected String doInBackground(String... params) {
            String imdbID = params[0];

            JsonReader reader;
            try {
                URL url = new URL(String.format(address, imdbID));
                InputStream is = url.openStream();
                reader = new JsonReader(new InputStreamReader(is));

                reader.beginObject();
                while(reader.hasNext()) {
                    String fieldName = reader.nextName();
                    if(fieldName.equals("Title")) {
                        result.setTitle(reader.nextString());
                        break;
                    }
                }
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
