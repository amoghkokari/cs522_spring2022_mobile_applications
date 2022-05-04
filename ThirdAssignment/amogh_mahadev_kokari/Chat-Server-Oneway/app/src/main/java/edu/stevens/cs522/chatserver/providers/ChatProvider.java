package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    /*
     * These are the patterns that are matched against the content URI that was specified.
     */

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    /*
     * Pattern-matching against the URI maps to one of these constant values for the content.
     */
    private static final int MESSAGES_ALL_ROWS = 1;

    private static final int MESSAGES_SINGLE_ROW = 2;

    private static final int PEERS_ALL_ROWS = 3;

    private static final int PEERS_SINGLE_ROW = 4;


    /*
     * Database metadata
     */

    private static final String DATABASE_NAME = "messages.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";


    /*
     * This helper class takes care of opening the dataase on disk
     * and initializing the database from its schema.
     */
    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /*
         * The Message table has a FK reference to the Peer table.
         * Since the FK is based on the peer name, an index is required in Peer.
         * An index is also recommended on the FK column in Message,
         * otherwise integrity checking can become very expensive.
         */
        private static final String CREATE_PEER_TABLE =
                "create table " + PEERS_TABLE + " ("
                        + PeerContract._ID + " integer primary key, "
                        + PeerContract.NAME + " text not null, "
                        + PeerContract.TIMESTAMP + " integer, "
                        + PeerContract.LATITUDE + " real, "
                        + PeerContract.LONGITUDE + " real "
                        + ");"
                + String.format("create unique index peer-name-index on %s(%s);",
                        PEERS_TABLE, PeerContract.NAME);
        private static final String CREATE_MESSAGE_TABLE =
                "create table " + MESSAGES_TABLE + " ("
                        + MessageContract._ID + " integer primary key, "
                        + MessageContract.CHATROOM + " text not null, "
                        + MessageContract.MESSAGE_TEXT + " text not null, "
                        + MessageContract.TIMESTAMP + " integer, "
                        + MessageContract.LATITUDE + " real, "
                        + MessageContract.LONGITUDE + " real, "
                        + MessageContract.SENDER + " text not null, "
                        + String.format("foreign key (%s) references %s(%s)",
                                        MessageContract.SENDER,
                                        PEERS_TABLE,
                                        PeerContract.NAME)
                        + ");"
                + String.format("create index message-sender-index on %s(%s);",
                        MESSAGES_TABLE, MessageContract.SENDER);

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(CREATE_PEER_TABLE);
            db.execSQL(CREATE_MESSAGE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Upgrade database if necessary
            throw new UnsupportedOperationException("Database upgrade not implemented.");
        }

    }

    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        /*
         * Initialize your content provider on startup, using SQLiteOpehHlper
         */
        databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    /*
     * Used to dispatch operation based on URI.
     */
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    /*
     * We use vendor-specific MIME types for content types.
     */
    protected String contentType(String content) {
        return "vnd.android.cursor/vnd." + BaseContract.AUTHORITY + "." + content + "s";
    }

    protected String contentItemType(String content) {
        return "vnd.android.cursor.item/vnd." + BaseContract.AUTHORITY + "." + content + "s";
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return contentType("message");
            case MESSAGES_SINGLE_ROW:
                return contentItemType("message");
            case PEERS_ALL_ROWS:
                return contentType("peer");
            case PEERS_SINGLE_ROW:
                return contentItemType("peer");
            default:
                throw new IllegalStateException("Unrecognized case.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                /*
                 * TODO: Implement this to handle requests to insert a new message.
                 * Make sure to notify any observers of this content!
                 */
//                throw new UnsupportedOperationException("Not yet implemented");
                long messageId = db.insert(MESSAGES_TABLE, null, values);
                Uri messageInstanceUri = MessageContract.CONTENT_URI(messageId);

                ContentResolver messageResolver = getContext().getContentResolver();
                messageResolver.notifyChange(messageInstanceUri, null);

                return messageInstanceUri;


                // End TODO

            case PEERS_ALL_ROWS:
                /*
                 * This handles requests to upsert (insert or update) a new peer.
                 * We should make sure to notify any observers, so loaders requery the DB.
                 *
                 * First, query for the specific peer record.
                 */
                String selection = PeerContract.NAME + "=?";
                String[] selectionArgs = new String[]{ values.getAsString(PeerContract.NAME) };
                Cursor cursor = query(uri, null, selection, selectionArgs, null);
                /*
                 * Now, check if the cursor is non-empty.  If so, update with recent info.
                 * Otherwise, insert a new record for this peer.
                 */
                long peerId;
                if (cursor.moveToFirst()) {
                    peerId = PeerContract.getId(cursor);
                    selection = PeerContract._ID + "=?";
                    selectionArgs = new String[]{ Long.toString(peerId) };
                    db.update(PEERS_TABLE, values, selection, selectionArgs);
                } else {
                    peerId = db.insert(PEERS_TABLE, null, values);
                }
                /*
                 * End by returning the URI for the row (new or not).
                 */
                return PeerContract.CONTENT_URI(peerId);

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                /*
                 * TODO: Implement this to handle query of all messages.
                 * Make sure to set this cursor to watch for content updates!
                 *
                 * The selection args may filter for messages for a particular peer.
                 */
//                throw new UnsupportedOperationException("Not yet implemented");
                Cursor messageCursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                messageCursor.setNotificationUri(getContext().getContentResolver(), uri);

                return messageCursor;
                // End TODO

            case PEERS_ALL_ROWS:
                /*
                  * Query for all peers.
                 */
                return db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

            default:
                throw new IllegalStateException("query: bad case");        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete not implemented");
    }

}
