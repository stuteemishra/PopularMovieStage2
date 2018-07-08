package com.example.stutee.popularmovieproject2;


import android.content.res.Configuration;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stutee.popularmovieproject2.data.FavoriteMovieContract;
import com.example.stutee.popularmovieproject2.utils.SetApiKey;
import com.example.stutee.popularmovieproject2.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,FavoriteMoviesCursorAdapter.FavoriteMovieAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    private int flag;

    private static final int MOVIE_LOADER_ID = 10;
    private static final int FAVORITE_MOVIES_LOADER_ID = 0;

    private static final String SEARCH_QUERY_URL_EXTRA = "queryExtra";

    private static final String KEY_FOR_TITLE = "title";
    private static final String KEY_FOR_DATE = "date";
    private static final String KEY_FOR_OVERVIEW = "overview";
    private static final String KEY_FOR_RATINGS = "ratings";
    private static final String KEY_FOR_ID = "id";
    private static final String KEY_FOR_POSTER_PATH = "poster_path";

    private Context context = this;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private FavoriteMoviesCursorAdapter mAdapter;
    public static RecyclerView mRecyclerViewForFavoriteMovies;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    String[] movieOriginalTitle;
    int[] movieId;
    String[] overviewOfMovie;
    String[] rating;
    String[] releaseDate;
    String[] completeInfo;
    String[] parsedMoviePosterPath = null;

    private String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag = 0;

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies);
        mRecyclerViewForFavoriteMovies = (RecyclerView) findViewById(R.id.rv_favorite_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);


        //GridLayoutManager for top rated and most popular movies poster.
        //int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
        int mNoOfColumns = Utilities.calculateNoOfColumns(getApplicationContext());
        GridLayoutManager layoutManager
                = new GridLayoutManager(this,mNoOfColumns);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);


        mMovieAdapter = new MovieAdapter(context,this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */

        GridLayoutManager layoutManagerForFavoriteMovies = new GridLayoutManager(this,2);

        mRecyclerViewForFavoriteMovies.setLayoutManager(layoutManagerForFavoriteMovies);

        mRecyclerViewForFavoriteMovies.setHasFixedSize(true);

        mAdapter = new FavoriteMoviesCursorAdapter(context,this);

        mRecyclerViewForFavoriteMovies.setAdapter(mAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //String queryParameter = "http://api.themoviedb.org/3/movie/popular?api_key="+ SetApiKey.apiKey;

        s = "http://api.themoviedb.org/3/movie/popular?api_key="+ SetApiKey.apiKey;

        int loaderId = MOVIE_LOADER_ID;

        Bundle bundleForLoader = new Bundle();
        //bundleForLoader.putString(SEARCH_QUERY_URL_EXTRA,queryParameter);

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, loaderForMovies);


    }


    @Override
    public void onClick(int id,String title,String overview,String releaseDate,String ratings,String posterpath) {

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Bundle extras = new Bundle();
        extras.putInt(KEY_FOR_ID,id);
        extras.putString(KEY_FOR_TITLE,title);
        extras.putString(KEY_FOR_OVERVIEW,overview);
        extras.putString(KEY_FOR_DATE,releaseDate);
        extras.putString(KEY_FOR_RATINGS,ratings);
        extras.putString(KEY_FOR_POSTER_PATH,posterpath);
        intentToStartDetailActivity.putExtras(extras);
        startActivity(intentToStartDetailActivity);

    }


    private void showPopularMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private LoaderCallbacks<String> loaderForMovies = new LoaderCallbacks<String>() {
        @NonNull
        @Override
        public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<String>(MainActivity.this) {

                String mMovieJsonResult;

                @Override
                protected void onStartLoading() {
                    if (mMovieJsonResult != null) {

                        deliverResult(mMovieJsonResult);

                    } else {

                        mLoadingIndicator.setVisibility(View.VISIBLE);

                        forceLoad();

                    }
                }

                @Override
                public String loadInBackground() {

                    //String queryParameter = args.getString(SEARCH_QUERY_URL_EXTRA);



                    Log.v(TAG, "poster path: " + s);

                    URL popularMovieRequestUrl = NetworkUtils.buildUrl(s);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(popularMovieRequestUrl);


                        return jsonMovieResponse;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(String data) {
                    mMovieJsonResult = data;
                    super.deliverResult(data);

                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<String> loader, String data) {

            if (data == null) {
                showErrorMessage();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerViewForFavoriteMovies.setVisibility(View.GONE);
            } else {
                final String SHOW_RESULTS = "results";
                final String ID_OF_MOVIE = "id";
                final String RATING = "vote_average";

                final String POSTER_PATH = "poster_path";
                final String ORIGINAL_TITLE = "original_title";

                final String OVERVIEW_OF_MOVIE = "overview";
                final String RELEASE_DATE = "release_date";


                /* String array to hold each day's weather String */
                try {

                    JSONObject popularMoviesJson = new JSONObject(data);

                    JSONArray jsonArray = popularMoviesJson.getJSONArray(SHOW_RESULTS);


                    parsedMoviePosterPath = new String[jsonArray.length()];
                    movieId = new int[jsonArray.length()];
                    movieOriginalTitle = new String[jsonArray.length()];
                    overviewOfMovie = new String[jsonArray.length()];
                    rating = new String[jsonArray.length()];
                    releaseDate = new String[jsonArray.length()];
                    completeInfo = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int movieIdInt = jsonObject.getInt(ID_OF_MOVIE);
                        String ratingString = jsonObject.getString(RATING);
                        String posterPathString = jsonObject.getString(POSTER_PATH);
                        String originalTitleString = jsonObject.getString(ORIGINAL_TITLE);
                        String overviewString = jsonObject.getString(OVERVIEW_OF_MOVIE);
                        String releaseDateString = jsonObject.getString(RELEASE_DATE);

                        parsedMoviePosterPath[i] = "http://image.tmdb.org/t/p/w342/" + posterPathString;
                        movieId[i] = movieIdInt;
                        movieOriginalTitle[i] = originalTitleString;
                        overviewOfMovie[i] = overviewString;
                        rating[i] = ratingString;
                        releaseDate[i] = releaseDateString;
                    }
                } catch (JSONException j) {
                    j.getStackTrace();
                }

                mMovieAdapter.setPopularMovieId(movieId);
                mMovieAdapter.setPopularMovieTitle(movieOriginalTitle);
                mMovieAdapter.setPopularMovieOverview(overviewOfMovie);
                mMovieAdapter.setPopularMovieDate(releaseDate);
                mMovieAdapter.setPopularMovieRatings(rating);

                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerViewForFavoriteMovies.setVisibility(View.GONE);

                showPopularMovieDataView();

                mMovieAdapter.setPopularMoviePath(parsedMoviePosterPath);

            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<String> loader) {

        }
    };

    //LoaderCallbacks<> for displaying favorite movies list items.


    private LoaderCallbacks<Cursor> favoriteMoviesLoaderListener = new LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<Cursor>(MainActivity.this) {

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

            // Update the data that the adapter uses to create ViewHolders
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.GONE);
            mRecyclerViewForFavoriteMovies.setVisibility(View.VISIBLE);
            mAdapter.swapCursor(data);

        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemThatWasClicked = item.getItemId();

        if(itemThatWasClicked == R.id.action_sort_by_popular){

            //String queryURL = "http://api.themoviedb.org/3/movie/popular?api_key="+ SetApiKey.apiKey;
            s = "http://api.themoviedb.org/3/movie/popular?api_key="+ SetApiKey.apiKey;
            Bundle bundleForLoader = new Bundle();
            //bundleForLoader.putString(SEARCH_QUERY_URL_EXTRA,queryURL);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundleForLoader, loaderForMovies);

            return true;
        }

        if(itemThatWasClicked == R.id.action_sort_by_top_rated){

            //String queryURL = "http://api.themoviedb.org/3/movie/top_rated?api_key="+ SetApiKey.apiKey;
            s = "http://api.themoviedb.org/3/movie/top_rated?api_key="+ SetApiKey.apiKey;
            Bundle bundleForLoader = new Bundle();
            //bundleForLoader.putString(SEARCH_QUERY_URL_EXTRA,queryURL);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundleForLoader,  loaderForMovies);
            return true;
        }

        if(itemThatWasClicked == R.id.action_sort_by_favorite){
            int loaderIdForFavoriteMovies = FAVORITE_MOVIES_LOADER_ID;
            Bundle bundle = new Bundle();
            getSupportLoaderManager().restartLoader(loaderIdForFavoriteMovies,bundle,favoriteMoviesLoaderListener);
            flag = flag+1;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickMovie(int movieId, String movieTitle,String movieOverview,String movieReleaseDate,String movieRatings,String moviePosterPath) {

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Bundle extras = new Bundle();
        extras.putInt(KEY_FOR_ID,movieId);
        extras.putString(KEY_FOR_TITLE,movieTitle);
        extras.putString(KEY_FOR_OVERVIEW,movieOverview);
        extras.putString(KEY_FOR_DATE,movieReleaseDate);
        extras.putString(KEY_FOR_RATINGS,movieRatings);
        extras.putString(KEY_FOR_POSTER_PATH,moviePosterPath);
        intentToStartDetailActivity.putExtras(extras);
        startActivity(intentToStartDetailActivity);


    }

    @Override
    protected void onResume() {
        if(flag == 0)
            super.onResume();
        else if(flag == 1)
        {
            int loaderIdForFavoriteMovies = FAVORITE_MOVIES_LOADER_ID;
            Bundle bundle = new Bundle();
            getSupportLoaderManager().restartLoader(loaderIdForFavoriteMovies,bundle,favoriteMoviesLoaderListener);
            super.onResume();
        }
    }


}

