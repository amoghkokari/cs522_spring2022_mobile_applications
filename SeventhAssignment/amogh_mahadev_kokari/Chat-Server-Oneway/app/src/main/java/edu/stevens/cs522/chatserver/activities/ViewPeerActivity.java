package edu.stevens.cs522.chatserver.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.TextAdapter;
import edu.stevens.cs522.chatserver.viewmodels.PeerViewModel;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity {

    public static final String TAG = ViewPeerActivity.class.getCanonicalName();

    public static final String PEER_KEY = "peer";

    private TextAdapter<Message> messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO Set the fields of the UI
        TextView userName = findViewById(R.id.view_user_name);
        TextView timeStamp = findViewById(R.id.view_timestamp);
        TextView locn = findViewById(R.id.view_location);

        userName.setText(String.format("User Name: %s", peer.name));
        timeStamp.setText(String.format("Last Seen: %s", formatTimestamp(peer.timestamp)));
        locn.setText(String.format("GPS: %s %s", peer.latitude.toString(), peer.longitude.toString()));
        // End TODO

        // Initialize the recyclerview and adapter for messages
        RecyclerView messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new TextAdapter<>(messageList);
        messageList.setAdapter(messagesAdapter);

        // TODO open the view model
        PeerViewModel peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);

        // TODO query the database asynchronously, and use messagesAdapter to display the result
        LiveData<List<Message>> messages = peerViewModel.fetchMessagesFromPeer(peer);

        Observer<List<Message>> observer = msg -> {
            messagesAdapter.setDataset(msg);
            messageList.setAdapter(messagesAdapter);
        };

        messages.observe(this, observer);
    }

    private static String formatTimestamp(Date timestamp) {
        LocalDateTime dateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
