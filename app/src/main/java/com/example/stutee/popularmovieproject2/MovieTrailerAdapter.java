package com.example.stutee.popularmovieproject2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    private String[] mPopularMovieTrailerKey;

    private Context mContext;

    private MovieTrailerAdapterOnClickHandler mClickHandler;

    public interface MovieTrailerAdapterOnClickHandler{
        void onClick(String movieTrailerKey);
    }


    public MovieTrailerAdapter(Context context, MovieTrailerAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;

    }


    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieTrailerViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull MovieTrailerViewHolder holder, int position) {

        holder.mTrailerView.setText("Play Trailer "+String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        if (null == mPopularMovieTrailerKey) return 0;
        return mPopularMovieTrailerKey.length;
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrailerView;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerView = (TextView) itemView.findViewById(R.id.tv_play_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String movieTrailerKey = mPopularMovieTrailerKey[adapterPosition];
            mClickHandler.onClick(movieTrailerKey);
        }
    }

    public void setMovieTrailerKey(String[] popularMovieTrailerKey) {
        mPopularMovieTrailerKey = popularMovieTrailerKey;
        notifyDataSetChanged();

    }


}
