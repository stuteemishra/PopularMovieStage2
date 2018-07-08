package com.example.stutee.popularmovieproject2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    /*
    * * This is the name of our database. Database names should be descriptive and end with the
    * * .db extension.
    * */

    public static final String DATABASE_NAME = "favorite_movies.db";

    /*
    * * If you change the database schema, you must increment the database version or the onUpgrade
    * * method will not be called.
     */

    private static final int DATABASE_VERSION = 4;



    public FavoriteMovieDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +

                        FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RATINGS + " TEXT NOT NULL, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        " UNIQUE (" + FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
        * * After we've spelled out our SQLite table creation statement above, we actually execute
        * * that SQL with the execSQL method of our SQLite database object.
        * */

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
