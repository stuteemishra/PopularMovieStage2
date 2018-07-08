package com.example.stutee.popularmovieproject2;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stutee.popularmovieproject2.data.FavoriteMovieContract;
import com.squareup.picasso.Picasso;

public class FavoriteMoviesCursorAdapter extends RecyclerView.Adapter<FavoriteMoviesCursorAdapter.FavoriteMovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    private FavoriteMovieAdapterOnClickHandler mClickHandler;

    public interface FavoriteMovieAdapterOnClickHandler{
        void onClickMovie(int movieId,String movieTitle,String movieOverview,String movieReleaseDate,String movieRatings,String moviePosterPath);
    }


    public FavoriteMoviesCursorAdapter(Context context,FavoriteMovieAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    public FavoriteMoviesCursorAdapter(Context context) {
        this.mContext = context;
    }


    @NonNull
    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favorite_movie_list_item, parent, false);
        return new FavoriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteMovieViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry._ID);
        int currentFavoriteMovieTitleIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_TITLE);
        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String currentFavoriteMovieTitle = mCursor.getString(currentFavoriteMovieTitleIndex);

        holder.itemView.setTag(id);
        holder.mMovieTitle.setText(currentFavoriteMovieTitle);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;

        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {

            this.notifyDataSetChanged();
        }
        return temp;
    }



    public class FavoriteMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mMovieTitle;
        public FavoriteMovieViewHolder(View itemView) {
            super(itemView);
            mMovieTitle = (TextView)itemView.findViewById(R.id.tv_favorite_movie_title);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int idIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID);
            int currentFavoriteMovieTitleIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_TITLE);
            int currentFavoriteMovieOverviewIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW);
            int currentFavoriteMovieReleaseDateIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RELEASE_DATE);
            int currentFavoriteMovieRatingsIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RATINGS);
            int currentFavoriteMoviePosterPathIndex = mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_POSTER_PATH);

            mCursor.moveToPosition(adapterPosition);

            int id = mCursor.getInt(idIndex);
            String currentFavoriteMovieTitle = mCursor.getString(currentFavoriteMovieTitleIndex);
            String currentFavoriteMovieOverview = mCursor.getString(currentFavoriteMovieOverviewIndex);
            String currentFavoriteMovieDate = mCursor.getString(currentFavoriteMovieReleaseDateIndex);
            String currentFavoriteMovieRatings = mCursor.getString(currentFavoriteMovieRatingsIndex);
            String currentFavoriteMoviePosterPath = mCursor.getString(currentFavoriteMoviePosterPathIndex);

            mClickHandler.onClickMovie(id,currentFavoriteMovieTitle,currentFavoriteMovieOverview,currentFavoriteMovieDate,currentFavoriteMovieRatings,currentFavoriteMoviePosterPath);

        }
    }
}
