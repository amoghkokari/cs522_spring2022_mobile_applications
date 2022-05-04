package edu.stevens.cs522.amogh_mahadev_kokari_chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.stevens.cs522.amogh_mahadev_kokari_chatserver.R;
import edu.stevens.cs522.amogh_mahadev_kokari_chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String PEERS_KEY = "peers";

    ArrayAdapter<Peer> peersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        ArrayList<Peer> peers = getIntent().getParcelableArrayListExtra(PEERS_KEY);

        if (peers == null) {
            throw new IllegalArgumentException("Missing list of peers!");
        }

        // TODO display the list of peers, set this activity as onClick listener

        peersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,peers);
        ListView peersView = findViewById(R.id.peer_list);
        peersView.setAdapter(peersAdapter);
        peersView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = peersAdapter.getItem(position);
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);
    }
}
