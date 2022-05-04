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

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.TextAdapter;
import edu.stevens.cs522.chatserver.viewmodels.ChatViewModel;
import edu.stevens.cs522.chatserver.viewmodels.PeerViewModel;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity {

    public static final String TAG = ViewPeerActivity.class.getCanonicalName();

    public static final String PEER_KEY = "peer";

    private PeerViewModel peerViewModel;

    private LiveData<List<Message>> messages;

    private TextAdapter<Message> messagesAdapter;

    private RecyclerView messageList;

    private Peer peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        // TODO init the UI

        TextView userName = findViewById(R.id.view_user_name);
        TextView timeStamp = findViewById(R.id.view_timestamp);
        TextView address = findViewById(R.id.view_address);


        userName.setText(peer.name);
        timeStamp.setText(peer.timestamp.toString());
        address.setText(peer.address.toString());


        // Initialize the recyclerview and adapter for messages
        messageList = findViewById(R.id.view_messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new TextAdapter<>(messageList);
        messageList.setAdapter(messagesAdapter);

        // TODO open the view model
        peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);

        // TODO query the database asynchronously, and use messagesAdapter to display the result
        messages = peerViewModel.fetchMessagesFromPeer(peer);

        Observer<List<Message>> observer = msg -> {
            messagesAdapter.setDataset(msg);
            messagesAdapter.notifyDataSetChanged();
        };

        messages.observe(this, observer);
    }

}
