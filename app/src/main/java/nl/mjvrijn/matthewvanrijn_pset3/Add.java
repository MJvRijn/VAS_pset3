package nl.mjvrijn.matthewvanrijn_pset3;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

public class Add extends AppCompatActivity {
    private ArrayList<Film> results;
    private RecyclerView recyclerView;
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

        results = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.add_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmsAdapter(results);
        recyclerView.setAdapter(adapter);

        field = (EditText) findViewById(R.id.add_search_field);
        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = field.getText().toString();
                    new APIConnection().execute(query);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private class APIConnection extends AsyncTask<String, Integer, String> {
        private String address = "http://www.omdbapi.com/?s=%s&type=movie";
        private ArrayList<Film> films = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            try {
                String query = URLEncoder.encode(params[0], "UTF-8");
                URL url = new URL(String.format(address, query));
                InputStream is = url.openStream();

                // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String json = s.hasNext() ? s.next() : "";
                System.out.println(json);

                JSONArray reader = new JSONObject(json).getJSONArray("Search");

                for(int i = 0; i < reader.length(); i++) {
                    Film f = new Film();
                    f.setTitle(reader.getJSONObject(i).getString("Title"));
                    f.setYear(reader.getJSONObject(i).getInt("Year"));

                    try {
                        URL poster_url = new URL(reader.getJSONObject(i).getString("Poster"));
                        f.setPoster(Drawable.createFromStream(poster_url.openStream(), null));
                    } catch(MalformedURLException e) {
                        Log.w(this.getClass().getSimpleName(), "Failed to get film poster");
                    }

                    films.add(f);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            results.clear();
            results.addAll(films);
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            super.onPostExecute(s);
        }
    }
}
