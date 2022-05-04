/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.databases.MessageDao;
import edu.stevens.cs522.chatserver.databases.PeerDao;
import edu.stevens.cs522.chatserver.entities.Chatroom;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.viewmodels.ChatViewModel;
import edu.stevens.cs522.chatserver.viewmodels.ChatroomViewModel;
import edu.stevens.cs522.chatserver.viewmodels.SharedViewModel;

public class ChatServerActivity extends AppCompatActivity implements ChatroomsFragment.IChatroomListener, MessagesFragment.IChatListener {

	final static public String TAG = ChatServerActivity.class.getCanonicalName();

    public final static String SENDER_NAME = "name";

    public final static String CHATROOM = "room";

    public final static String MESSAGE_TEXT = "text";

    public final static String TIMESTAMP = "timestamp";

    public final static String LATITUDE = "latitude";

    public final static String LONGITUDE = "longitude";

    /*
     * Fragments for two-pane UI
     */
    private final static String SHOWING_CHATROOMS_TAG = "INDEX-FRAGMENT";

    private final static String SHOWING_MESSAGES_TAG = "CHAT-FRAGMENT";

    private boolean isTwoPane;

    /*
     * Shared with both the index and detail fragments
     */
    private SharedViewModel sharedViewModel;

    /*
     * Provides the operations for inserting messages and upsertig peers.
     */
    private ChatDatabase chatDatabase;


    /*
	 * Socket used both for sending and receiving.
	 *
	 * This should also be in a view model!
	 */
    private DatagramSendReceive serverSocket;
//  private DatagramSocket serverSocket;


    /*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "(Re)starting ChatServer activity....");

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
             * Get port information from the resources.
             */
            int port = getResources().getInteger(R.integer.app_port);

            // serverSocket = new DatagramSocket(port);

            serverSocket = new DatagramSendReceive(port);

        } catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
        }

        /*
         * Initialize the UI with the index and details fragments
         */
        setContentView(R.layout.chat_activity);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        if (!isTwoPane) {
            // Add an index fragment as the fragment in the frame layout (single-pane layout)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ChatroomsFragment(),SHOWING_CHATROOMS_TAG)
                    // Don't add this (why not?): .addToBackStack(SHOWING_CHATROOMS_TAG)
                    .commit();
        }
        // TODO get shared view model for current chatroom
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // TODO get database reference (for insertions)
        chatDatabase  = ChatDatabase.getInstance(getApplicationContext());
    }

	@Override
    /**
     * Called by the MessagesFragment to get the next message (synchronously!)
     */
    public void nextMessage() {
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {

            String sender = null;

            String room = null;

            String text = null;

            Date timestamp = null;

            Double latitude = null;

            Double longitude = null;

            /*
             * THere is an apparent bug in the emulator stack on Windows where
             * messages can arrive empty, we loop as a workaround.
             */

            while (sender == null) {

                Log.d(TAG, "Waiting for a message....");
                serverSocket.receive(receivePacket);
                Log.d(TAG, "Received a packet");

                if (receivePacket.getLength() == 0) {
                    Log.d(TAG, "....zero-length packet, skipping....");
                    continue;
                }

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                Log.d(TAG, "Source IP Address: " + address + " , Port: " + port);

                String content = new String(receivePacket.getData(), 0, receivePacket.getLength());
                Log.d(TAG, "Message received: " + content);

                /*
                 * Parse the JSON object
                 */
                JsonReader rd = new JsonReader(new StringReader(content));

                rd.beginObject();
                if (SENDER_NAME.equals(rd.nextName())) {
                    sender = rd.nextString();
                }
                if (CHATROOM.equals(rd.nextName())) {
                    room = rd.nextString();
                }
                if (MESSAGE_TEXT.equals((rd.nextName()))) {
                    text = rd.nextString();
                }
                if (TIMESTAMP.equals(rd.nextName())) {
                    timestamp = new Date(rd.nextLong());
                }
                if (LATITUDE.equals(rd.nextName())) {
                    latitude = rd.nextDouble();
                }
                if (LONGITUDE.equals((rd.nextName()))) {
                    longitude = rd.nextDouble();
                }
                rd.endObject();

                rd.close();

            }

            /*
             * Add the sender to our list of senders
             */
            Chatroom chatroom = new Chatroom();
            chatroom.name = room;

            Peer peer = new Peer();
            peer.name = sender;

            peer.timestamp = timestamp;
            peer.latitude = latitude;
            peer.longitude = longitude;

            Message message = new Message();
            message.messageText = text;
            message.chatroom = room;
            message.sender = sender;
            message.timestamp = timestamp;
            message.latitude = latitude;
            message.longitude = longitude;

            /*
			 * TODO upsert chatroom and peer, and insert message into the database
			 */
            chatDatabase.chatroomDao().insert(chatroom);
            chatDatabase.peerDao().upsert(peer);
            chatDatabase.messageDao().persist(message);
            /*
             * End TODO
             *
             * The livedata for the messages should update via observer automatically.
             */

            /*
             * Let the user know which chatroom received a message.
             */
            String updateMessage = getString(R.string.message_received, message.chatroom);
            Toast.makeText(this, updateMessage, Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			
			Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
			socketOK = false;
		} 

	}

	/*
	 * Close the socket before exiting application
	 */
	public void closeSocket() {
	    if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
	}

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
        Log.i(TAG, "Leaving ChatServer activity....");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS option
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.peers) {
            // TODO PEERS provide the UI for viewing list of peers
            // The subactivity will query the database for the list of peers.
            Intent intent = new Intent(this, ViewPeersActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    /**
     * Called by the ChatroomsFragment when a chatroom is selected.
     *
     * For two-pane UI, do nothing, but for single-pane, need to push the detail fragment.
     */
    public void setChatroom(Chatroom chatroom) {
        sharedViewModel.select(chatroom);
        if (!isTwoPane) {
            // TODO For single pane, replace chatrooms fragment with messages fragment.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new MessagesFragment())
                    .addToBackStack(SHOWING_CHATROOMS_TAG)
                    .commit();
            // Add chatrooms fragment to backstack, so pressing BACK key will return to index.
        }
    }

    @Override
    public void onBackPressed() {
        if (!isTwoPane) {
            super.onBackPressed();
            return;
        }
        /*
         * We are in two-pane mode, is a chatroom selected?
         */
        if (sharedViewModel.getSelected() == null) {
            super.onBackPressed();
            return;
        }
        /*
         * Unset the currently selected chatroom (in two-pane mode).
         */
        setChatroom(null);
    }

}