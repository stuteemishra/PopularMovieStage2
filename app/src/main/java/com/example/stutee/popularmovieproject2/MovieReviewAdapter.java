package com.example.stutee.popularmovieproject2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private String[] mPopularMovieReview;
    private String[] mReviewAuthor;

    private Context mContext;


    public MovieReviewAdapter(){

    }


    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieReviewViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {

        String currentReviewAuthor = mReviewAuthor[position];
        holder.mReviewAuthorView.setText(currentReviewAuthor);

        String currentReview = mPopularMovieReview[position];
        holder.mReview.setText(currentReview);

    }

    @Override
    public int getItemCount() {
        if (null == mPopularMovieReview) return 0;
        return mPopularMovieReview.length;
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthorView;
        public final TextView mReview;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorView = (TextView) itemView.findViewById(R.id.review_author);
            mReview = (TextView) itemView.findViewById(R.id.review);
        }

    }

    public void setMovieReviewAuthor(String[] reviewAuthor) {
        mReviewAuthor = reviewAuthor;
        notifyDataSetChanged();

    }

    public void setMovieReview(String[] review) {
        mPopularMovieReview = review;
        notifyDataSetChanged();

    }


}
