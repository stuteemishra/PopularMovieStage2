package com.example.stutee.popularmovieproject2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteMovieProvider extends ContentProvider {

    public static final int CODE_FAVORITE_MOVIE = 100;
    public static final int CODE_FAVORITE_MOVIE_BY_ID = 101;

    /*
    * * The URI Matcher used by this content provider. The leading "s" in this variable name
    * * signifies that this UriMatcher is a static member variable of FavoriteMovieProvider and is a
    * * common convention in Android programming.
    * */

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavoriteMovieDbHelper mOpenHelper;


    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = FavoriteMovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE_MOVIE, CODE_FAVORITE_MOVIE);
        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE_MOVIE + "/#", CODE_FAVORITE_MOVIE_BY_ID);

        return matcher;

    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_FAVORITE_MOVIE: {

                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned


        switch (match) {

            case CODE_FAVORITE_MOVIE:

                long id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesUnfavorite; // starts as 0

        switch (match) {

            // Handle the single item case, recognized by the ID included in the URI path
            case CODE_FAVORITE_MOVIE_BY_ID:

                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                moviesUnfavorite = db.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, "movie_id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesUnfavorite != 0) {
            //set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesUnfavorite;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
