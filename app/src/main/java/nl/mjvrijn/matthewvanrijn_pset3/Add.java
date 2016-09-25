package nl.mjvrijn.matthewvanrijn_pset3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Add extends AppCompatActivity {
    private ArrayList<Film> results = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.add_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmsAdapter(results);
        recyclerView.setAdapter(adapter);

        // Get the search field and attach a listener to it to execute the search when the
        // keyboard's search button is pressed.
        field = (EditText) findViewById(R.id.add_search_field);
        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = field.getText().toString().trim();
                    new SearchTask().execute(query);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /* Definition of an asynchronous task to create a list of Film objects from a query, using the OMDB api */
    private class SearchTask extends AsyncTask<String, Integer, String> {
        private ArrayList<Film> films = new ArrayList<>();
        private String query;
        private boolean result = true;

        /* Depending on the internet connection and OMDB server, the search can take a non-negligible
         * amount of time. To provide the user feedback that the search has been stared, display a
         * toast. */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(Add.this, "Searching...", Toast.LENGTH_SHORT).show();
        }

        /* Get the search result and make the list of Films asynchronously. */
        @Override
        protected String doInBackground(String... params) {
            try {
                query = URLEncoder.encode(params[0], "UTF-8");
                URL url = new URL(String.format("http://www.omdbapi.com/?s=%s&type=movie", query));
                JSONObject json = new JSONObject(Utils.inputSteamToString(url.openStream()));

                if (json.getString("Response").equals("True")) {
                    JSONArray array = json.getJSONArray("Search");

                    for (int i = 0; i < array.length(); i++) {
                        films.add(new Film(getCacheDir(), array.getJSONObject(i)));
                    }
                } else {
                    result = false;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /* If the search returned a result, update the data set, notify the adapter and scroll to
         * the top of the recyclerview. If not, tell the user with a toast. */
        @Override
        protected void onPostExecute(String s) {
            if(!result) {
                String message = "No results found for \"" + query + "\"";
                Toast.makeText(Add.this, message, Toast.LENGTH_LONG).show();
                Log.i("Networking", message + query + "\"");
            } else {
                results.clear();
                results.addAll(films);
                adapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(0);
            }

            super.onPostExecute(s);
        }
    }
}
