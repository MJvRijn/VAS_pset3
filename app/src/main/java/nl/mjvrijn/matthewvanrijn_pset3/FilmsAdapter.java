package nl.mjvrijn.matthewvanrijn_pset3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.ViewHolder> {
    private ArrayList<Film> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public ViewHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.film_text);

        }
    }

    public FilmsAdapter(ArrayList<Film> dataSet) {
        data = dataSet;
    }

    public void addData(Film f) {
        data.add(f);
        notifyDataSetChanged();
    }

    @Override
    public FilmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.films_card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int pos) {
        vh.text.setText(data.get(pos).getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
