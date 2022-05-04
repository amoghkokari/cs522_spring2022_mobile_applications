package edu.stevens.cs522.amogh_mahadev_kokari_chatserver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import edu.stevens.cs522.amogh_mahadev_kokari_chatserver.R;
import edu.stevens.cs522.amogh_mahadev_kokari_chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO Set the fields of the UI

        TextView userName = this.findViewById(R.id.view_user_name);
        TextView LastSeen = this.findViewById(R.id.view_timestamp);
        TextView locn = this.findViewById(R.id.view_location);

        userName.setText(String.format("Peer Name : %s", peer.name));
        LastSeen.setText(String.format("Last Seen : %s", formatTimestamp(peer.timestamp)));
        locn.setText(String.format("Peer Location : %s %s", peer.latitude, peer.longitude));
    }

    private static String formatTimestamp(Date timestamp) {
        LocalDateTime dateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

}
