package nl.mjvrijn.matthewvanrijn_pset3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.ViewHolder> {
    private ArrayList<Film> data;
    private View.OnClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public ImageView poster;
        public int pos;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.film_text);
            year = (TextView) v.findViewById(R.id.film_year);
            poster = (ImageView) v.findViewById(R.id.film_poster);

        }
    }

    public FilmsAdapter(ArrayList<Film> dataSet) {
        data = dataSet;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                int pos = ((RecyclerView.LayoutParams) v.getLayoutParams()).getViewLayoutPosition();
                Intent i = new Intent(c, Details.class);
                i.putExtra("film", data.get(pos));
                c.startActivity(i);
            }
        };
    }


    @Override
    public FilmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.films_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int pos) {
        vh.pos = pos;
        vh.title.setText(data.get(pos).getTitle());
        vh.year.setText("" + data.get(pos).getYear());
        vh.poster.setImageBitmap(data.get(pos).getPoster());
        vh.itemView.setOnClickListener(listener);
        // set onclicklistener to itemview
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
