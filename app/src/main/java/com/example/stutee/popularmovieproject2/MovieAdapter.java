package com.example.stutee.popularmovieproject2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PopularMovieAdapterViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private String[] mPopularMoviePath;
    private String[] mPopularMovieTitle;
    private String[] mPopularMovieDate;
    private String[] mPopularMovieOverview;
    private String[] mPopularMovieRatings;
    private int[] mPopularMovieId;


    private Context mContext;

    private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler{
        void onClick(int movieId,String movieTitle,String movieOverview,String movieReleaseDate,String movieRatings,String moviePosterpath);
    }


    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;

    }


    public class PopularMovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMoviePoster;

        public PopularMovieAdapterViewHolder(View view) {
            super(view);
            mMoviePoster = (ImageView) view.findViewById(R.id.tv_movie_poster);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String titleOfMovie = mPopularMovieTitle[adapterPosition];
            int idOfMovie = mPopularMovieId[adapterPosition];
            String overviewOfMovie = mPopularMovieOverview[adapterPosition];
            String ratingsOfMovie = mPopularMovieRatings[adapterPosition];
            String releaseDateOfMovie = mPopularMovieDate[adapterPosition];
            String posterPathOfMovie = mPopularMoviePath[adapterPosition];
            Log.v(TAG, "id: " + idOfMovie);
            mClickHandler.onClick(idOfMovie,titleOfMovie,overviewOfMovie,releaseDateOfMovie,ratingsOfMovie,posterPathOfMovie);

        }


    }

    @Override
    public PopularMovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PopularMovieAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PopularMovieAdapterViewHolder popularMovieAdapterViewHolder, int position) {
        String currentPosterPath = mPopularMoviePath[position];
        Log.v(TAG, "poster path: " + currentPosterPath);
        Picasso.with(mContext).load(currentPosterPath).into(popularMovieAdapterViewHolder.mMoviePoster);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     */
    @Override
    public int getItemCount() {
        if (null == mPopularMoviePath) return 0;
        return mPopularMoviePath.length;
    }


    public void setPopularMoviePath(String[] popularMoviePath) {

        mPopularMoviePath = popularMoviePath;
        notifyDataSetChanged();

    }

    public void setPopularMovieTitle(String[] popularMovieTitle) {

        mPopularMovieTitle = popularMovieTitle;

    }

    public void setPopularMovieDate(String[] popularMovieDate) {

        mPopularMovieDate = popularMovieDate;

    }

    public void setPopularMovieRatings(String[] popularMovieRatings) {

        mPopularMovieRatings = popularMovieRatings;

    }

    public void setPopularMovieOverview(String[] popularMovieOverview) {

        mPopularMovieOverview = popularMovieOverview;

    }

    public void setPopularMovieId(int[] popularMovieId) {

        mPopularMovieId = popularMovieId;

    }

}
