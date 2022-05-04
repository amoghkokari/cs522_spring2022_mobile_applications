package edu.stevens.cs522.chatserver.contracts;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class BaseContract implements BaseColumns {

    public static final String AUTHORITY = "edu.stevens.cs522.chatserver";

    /*
     * Builds a URI for content, based on the authority of the content provider
     * and the context path (table name) for a specific part of the content.
     */
    public static final Uri CONTENT_URI(String authority, String path) {
        return new Uri.Builder().scheme("content")
                .authority(authority)
                .path(path)
                .build();
    }

    /*
     * Extends a context path, typically used to extend the URI for a table
     * to the URI for a row in the table, by adding the PK at the end of the URI.
     */
    public static Uri withExtendedPath(Uri uri,
                                       String... path) {
        Uri.Builder builder = uri.buildUpon();
        for (String p : path)
            builder.appendPath(p);
        return builder.build();
    }

    /*
     * Applied to the URI for a row, extracts the PK from the last segment.
     */
    public static final long getId(Uri uri) {
        return Long.parseLong(uri.getLastPathSegment());
    }

    /*
     * Extracts a content path from a URI (used in content provider
     * to pattern-match a URI against content paths).
     */
    public static final String CONTENT_PATH(Uri uri) {
        return uri.getPath().substring(1);
    }



}
