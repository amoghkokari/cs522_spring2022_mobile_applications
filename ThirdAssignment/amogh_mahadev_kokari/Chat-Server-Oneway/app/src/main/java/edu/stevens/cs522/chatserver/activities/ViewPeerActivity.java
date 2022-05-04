package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ViewPeerActivity.class.getCanonicalName();

    public static final String PEER_KEY = "peer";

    private Peer peer;

    /*
     * UI for messages sent by this peer
     */
    private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    static final private int LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO Set the fields of the UI
        TextView username = this.findViewById(R.id.view_user_name);
        TextView timestamp = this.findViewById(R.id.view_timestamp);
        TextView locn = this.findViewById(R.id.view_location);

        username.setText(String.format("Sender name : %s", peer.name));
        timestamp.setText(String.format("Last Seen   : %s", formatTimestamp(peer.timestamp)));
        locn.setText(String.format("Location     : %s  %s", peer.latitude, peer.longitude));

        String[] to = new String[] {MessageContract.MESSAGE_TEXT};
        int[] from = new int[] { R.id.message };


        // TODO use SimpleCursorAdapter (with flags=0 and null initial cursor) to display the messages received.
        // You can use android.R.simple_list_item_1 as layout for each row.

        messagesAdapter = new SimpleCursorAdapter(this, R.layout.message, null, to, from,0);
        messageList = this.findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);


        // TODO Use loader manager to initiate a query of the database
        // Make sure to use the Jetpack library, not the deprecated core implementation.

        Bundle peerBundle = new Bundle();
        peerBundle.putLong("peer_id", peer.id);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, peerBundle, this);
    }

    private static String formatTimestamp(Date timestamp) {
        LocalDateTime dateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                String messageSelection = (MessageContract.SENDER + "=?");
                String[] messageSelectionArgs = { peer.name };
                // TODO use a CursorLoader to initiate a query on the database
                return new CursorLoader(this, MessageContract.CONTENT_URI, null,
                        messageSelection, messageSelectionArgs, null);

            default:
                throw new IllegalStateException(("Unexpected loader id: " + id));
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // TODO populate the UI with the result of querying the provider
        messagesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // TODO reset the UI when the cursor is empty
        messagesAdapter.swapCursor(null);
    }
}
