package com.example.stutee.popularmovieproject2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.stutee.popularmovieproject2.data.FavoriteMovieContract;
import com.example.stutee.popularmovieproject2.utils.SetApiKey;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String KEY_FOR_ID = "id";

    private static final int DETAIL_LOADER_ID = 9;

    private static final int LOADER_ID = 28;

    private ImageView mDisplayImage;


    private TextView mDisplayTitle;
    private TextView mDisplayOverview;
    private TextView mDisplayReleaseDate;
    private TextView mDisplayRatings;
    private TextView mDisplayErrorMessage;
    private TextView mNoReview;
    private TextView mNoTrailer;
    private TextView mFavoriteMovie;
    private TextView mUnFavorite;

    private int mMovieId;
    String mTitle;
    String mOverview;
    String mDate;
    String mRatings;
    String mPath;
    private String[] authorOfReview;
    private String[] reviewOfMovie;
    private String[] keyOfTrailer;
    private String review;
    private String showErrorMessage;

    Context mContext = this;

    private LoadTrailerTask mLoadTrailerTask;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewForReview;

    private MovieTrailerAdapter mAdapter;
    private MovieReviewAdapter mAdapterForReview;
    private FavoriteMoviesCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        int loaderId = DETAIL_LOADER_ID;

        mDisplayImage = (ImageView) findViewById(R.id.poster_image_view);

        mDisplayTitle = (TextView) findViewById(R.id.tv_title);
        mDisplayOverview = (TextView) findViewById(R.id.tv_overview);
        mDisplayReleaseDate = (TextView) findViewById(R.id.tv_date);
        mDisplayRatings = (TextView) findViewById(R.id.tv_ratings);
        mNoReview = (TextView) findViewById(R.id.tv_no_review);
        mDisplayErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mNoTrailer = (TextView) findViewById(R.id.tv_for_no_trailer);
        mFavoriteMovie = (TextView) findViewById(R.id.mark_favorite);
        mUnFavorite = (TextView) findViewById(R.id.mark_unfavorite);

        mAdapter = new MovieTrailerAdapter(mContext,this);

        mCursorAdapter = new FavoriteMoviesCursorAdapter(mContext);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies_trailer);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);


        mAdapterForReview = new MovieReviewAdapter();
        mRecyclerViewForReview = (RecyclerView) findViewById(R.id.rv_popular_movies_review);
        LinearLayoutManager layoutManagerForReview
                = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecyclerViewForReview.setLayoutManager(layoutManagerForReview);
        mRecyclerViewForReview.setHasFixedSize(true);
        mRecyclerViewForReview.setAdapter(mAdapterForReview);




        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {

            Bundle extras = intentThatStartedThisActivity.getExtras();
            mMovieId = extras.getInt(KEY_FOR_ID);
            mTitle = extras.getString("title");
            mOverview = extras.getString("overview");
            mDate = extras.getString("date");
            mRatings = extras.getString("ratings");
            mPath = extras.getString("poster_path");

            Picasso.with(mContext).load(mPath).placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(mDisplayImage);
            mDisplayTitle.setText(mTitle);
            mDisplayOverview.setText(mOverview);
            mDisplayReleaseDate.setText(mDate);
            mDisplayRatings.setText(mRatings);
        }

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, reviewLoaderListener);

        showErrorMessage = getString(R.string.error_message);

        String queryUrl = "http://api.themoviedb.org/3/movie/"+mMovieId+"/videos?api_key="+ SetApiKey.apiKey;

        mLoadTrailerTask = new LoadTrailerTask();
        mLoadTrailerTask.execute(queryUrl);

        mFavoriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID, mMovieId);
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_TITLE, mTitle);
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW,mOverview);
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RELEASE_DATE,mDate);
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RATINGS,mRatings);
                contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_POSTER_PATH,mPath);

                // Insert the content values via a ContentResolver
                Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
                if(uri != null) {
                    Toast.makeText(getBaseContext(), "Marked Favorite.", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"uri----> "+uri);
                    mUnFavorite.setVisibility(View.VISIBLE);
                    mFavoriteMovie.setVisibility(View.INVISIBLE);
                }
            }
        });



        mUnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = 0;//Initially zero
                String stringMovieId = Integer.toString(mMovieId);

                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;

                uri = uri.buildUpon().appendPath(stringMovieId).build();

                count = getContentResolver().delete(uri, null, null);

                if(count != 0){
                    Toast.makeText(getBaseContext(), "Marked UnFavorite.", Toast.LENGTH_SHORT).show();
                    mFavoriteMovie.setVisibility(View.VISIBLE);
                    mUnFavorite.setVisibility(View.INVISIBLE);

                }
                else if(count == 0){
                    Toast.makeText(getBaseContext(), "Not a Favorite Movie.Cannot UnFavorite.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        String[] mSelectionArgs = new String[]{""+mMovieId};

        Cursor c = getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + "=?",
                mSelectionArgs,
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID);
        Log.v(TAG,"cursor----> "+c);
        if(c == null){

            Log.v(TAG,"error "+c);

        }
        else if(c.getCount()<1){
            Log.v(TAG,"no element "+c);
            mFavoriteMovie.setVisibility(View.VISIBLE);
            mUnFavorite.setVisibility(View.INVISIBLE);
        }
        else{

            mUnFavorite.setVisibility(View.VISIBLE);
            mFavoriteMovie.setVisibility(View.INVISIBLE);


        }


    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderListener = new LoaderManager.LoaderCallbacks<Cursor>(){

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<Cursor>(DetailActivity.this) {

                Cursor mFavoriteMovieData = null;

                @Override
                protected void onStartLoading() {
                    if (mFavoriteMovieData != null) {
                        // Delivers any previously loaded data immediately
                        deliverResult(mFavoriteMovieData);
                    } else {
                        // Force a new load
                        forceLoad();
                    }
                }
                @Override
                public Cursor loadInBackground() {

                    try {

                        return getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");

                        e.printStackTrace();

                        return null;

                    }

                }
                // deliverResult sends the result of the load, a Cursor, to the registered listener
                public void deliverResult(Cursor data) {
                    mFavoriteMovieData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

            mCursorAdapter.swapCursor(data);

        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(String movieTrailerKey) {

        String trailerUrl = "https://youtu.be/"+movieTrailerKey;
        Uri webPage = Uri.parse(trailerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW,webPage);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }



    private LoaderManager.LoaderCallbacks<String> reviewLoaderListener = new LoaderManager.LoaderCallbacks<String>(){

        @NonNull
        @Override
        public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<String>(DetailActivity.this) {

                String mDetailDataJsonResponse;

                @Override
                protected void onStartLoading() {

                    if(mDetailDataJsonResponse != null){
                        deliverResult(mDetailDataJsonResponse);
                    }
                    else
                        forceLoad();
                }

                @Nullable
                @Override
                public String loadInBackground() {
                    String queryUrl = "http://api.themoviedb.org/3/movie/"+mMovieId+"/reviews?api_key="+ SetApiKey.apiKey;
                    Log.v(TAG,"review url"+queryUrl);
                    URL popularMovieReviewUrl = NetworkUtils.buildUrl(queryUrl);

                    try {
                        String jsonMovieDetailResponse = NetworkUtils
                                .getResponseFromHttpUrl(popularMovieReviewUrl);


                        return jsonMovieDetailResponse;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(String data) {
                    mDetailDataJsonResponse = data;
                    super.deliverResult(data);

                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<String> loader, String data) {
            if(data == null){
                mRecyclerViewForReview.setVisibility(View.INVISIBLE);
                mDisplayErrorMessage.setText(showErrorMessage);
                mDisplayErrorMessage.setVisibility(View.VISIBLE);

            }
            else{

                mDisplayErrorMessage.setVisibility(View.INVISIBLE);
                mRecyclerViewForReview.setVisibility(View.VISIBLE);

                Log.v(TAG,"data  "+data);
                final String SHOW_RESULTS = "results";
                final String AUTHOR = "author";
                final String CONTENT = "content";

                try {

                    JSONObject moviesExtraDetailJson = new JSONObject(data);

                    JSONArray jsonArray = moviesExtraDetailJson.getJSONArray(SHOW_RESULTS);
                    authorOfReview = new String[jsonArray.length()];
                    reviewOfMovie = new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String authorOfMovieReview = jsonObject.getString(AUTHOR);
                        String reviewOfTheMovie = jsonObject.getString(CONTENT);

                        authorOfReview[i] = authorOfMovieReview;
                        reviewOfMovie[i] = reviewOfTheMovie;

                        review = authorOfMovieReview+" : "+"\n\n"+reviewOfTheMovie;

                    }

                }catch (JSONException e){
                    e.getStackTrace();
                }
                if(review == null){
                    mRecyclerViewForReview.setVisibility(View.INVISIBLE);
                    mDisplayErrorMessage.setVisibility(View.INVISIBLE);
                    mNoReview.setVisibility(View.VISIBLE);
                    mNoReview.setText(getString(R.string.action_no_review));
                }
                else{
                    mAdapterForReview.setMovieReviewAuthor(authorOfReview);
                    mAdapterForReview.setMovieReview(reviewOfMovie);}

            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<String> loader) {

        }
    };



    public class LoadTrailerTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String queryUrl = strings[0];
            Log.v(TAG,"review url"+queryUrl);
            URL popularMovieTrailerUrl = NetworkUtils.buildUrl(queryUrl);

            try {
                String jsonMovieTrailerDetailResponse = NetworkUtils
                        .getResponseFromHttpUrl(popularMovieTrailerUrl);


                return jsonMovieTrailerDetailResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {

                mNoTrailer.setText(showErrorMessage);

            } else {
                Log.v(TAG, "data  " + s);
                final String SHOW_RESULTS = "results";
                final String KEY = "key";

                try {

                    JSONObject moviesExtraDetailJson = new JSONObject(s);

                    JSONArray jsonArray = moviesExtraDetailJson.getJSONArray(SHOW_RESULTS);
                    keyOfTrailer = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String keyOfMovieTrailer = jsonObject.getString(KEY);

                        keyOfTrailer[i] = keyOfMovieTrailer;

                    }

                    if (keyOfTrailer.length == 0) {
                        mNoTrailer.setText(getString(R.string.action_no_trailer));
                    }
                } catch (JSONException e) {
                    e.getStackTrace();
                }

                mAdapter.setMovieTrailerKey(keyOfTrailer);
            }
        }
    }





}
