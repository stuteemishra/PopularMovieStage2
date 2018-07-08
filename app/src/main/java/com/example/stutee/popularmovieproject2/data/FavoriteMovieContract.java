package com.example.stutee.popularmovieproject2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMovieContract {

    /*
     * * The "Content authority" is a name for the entire content provider, similar to the

     * relationship between a domain name and its website. A convenient string to use for the

     * content authority is the package name for the app, which is guaranteed to be unique on the

     * Play Store.

     */

    public static final String CONTENT_AUTHORITY = "com.example.stutee.popularmovieproject2";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * * the content provider for PopularMovies.
     * */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE_MOVIE = "favorite_movies";



    /* Inner class that defines the table contents of the weather table */

    public static final class FavoriteMovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIE)
                .build();

        /* Used internally as the name of our weather table. */
        public static final String TABLE_NAME = "favorite_movies";

        /* Movie ID as returned by API */
        public static final String MOVIE_ID = "movie_id";

        /* Movie title as returned by API */
        public static final String MOVIE_TITLE = "movie_title";

        /* Movie overview as returned by API */
        public static final String MOVIE_OVERVIEW = "movie_overview";

        /* Movie release date as returned by API */
        public static final String MOVIE_RELEASE_DATE = "movie_date";

        /* Movie ratings as returned by API */
        public static final String MOVIE_RATINGS = "movie_ratings";

        public static final String MOVIE_POSTER_PATH = "movie_poster";

    }


}
