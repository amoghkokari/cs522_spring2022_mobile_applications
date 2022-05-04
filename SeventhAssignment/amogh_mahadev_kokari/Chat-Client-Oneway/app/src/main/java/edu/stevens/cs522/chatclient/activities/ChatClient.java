/*********************************************************************

    Client for sending chat messages to the server..

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chatclient.activities;

import java.io.IOException;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatclient.R;
import edu.stevens.cs522.chatclient.dialog.SendMessage;
import edu.stevens.cs522.chatclient.location.CurrentLocation;

/*
 * @author dduggan
 * 
 */
public class ChatClient extends AppCompatActivity implements OnClickListener, SendMessage.IMessageSender {

	final static private String TAG = ChatClient.class.getCanonicalName();

	public final static String SENDER_NAME = "name";

	public final static String CHATROOM = "room";

	public final static String MESSAGE_TEXT = "text";

	public final static String TIMESTAMP = "timestamp";

	public final static String LATITUDE = "latitude";

	public final static String LONGITUDE = "longitude";

	/*
	 * Tag for dialog fragment
	 */
	private final static String ADDING_MESSAGE_TAG = "ADD-MESSAGE-DIALOG";

	/*
	 * Socket used for sending
	 */
//  private DatagramSocket clientSocket;
    private DatagramSendReceive clientSocket;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_client);
		
		/**
		 * Let's be clear, this is a HACK to allow you to do network communication on the chat_client thread.
		 * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
		 * this right in a future assignment (using a Service managing background threads).
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);

		FloatingActionButton sendButton = findViewById(R.id.send_button);
		sendButton.setOnClickListener(this);

		try {

			int port = getResources().getInteger(R.integer.app_port);
            clientSocket = new DatagramSendReceive(port);
            // clientSocket = new DatagramSocket(port);

		} catch (IOException e) {

		    throw new IllegalStateException("Cannot open socket", e);

		}

	}

	@Override
	/*
	 * Callback for the floating action button.
	 */
	public void onClick(View v) {
		SendMessage.launch(this, ADDING_MESSAGE_TAG);
	}

	@Override
	/*
	 * Callback for the dialog to send a message.
	 */
	public void send(String destAddrString, String chatroomName, String clientName, String text) {
		try {
			/*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

			InetAddress destAddr = InetAddressUtils.fromString(destAddrString);

			int destPort = getResources().getInteger(R.integer.app_port);

			Date timestamp = DateUtils.now();

			CurrentLocation location = CurrentLocation.getLocation(this);

			Log.d(TAG, String.format("Sending data from address %s:%d", clientSocket.getInetAddress(), clientSocket.getPort()));

			StringWriter output = new StringWriter();
			JsonWriter wr = new JsonWriter(output);
			wr.beginObject();
			wr.name(SENDER_NAME).value(clientName);
			wr.name(CHATROOM).value(chatroomName);
			wr.name(MESSAGE_TEXT).value(text);
			wr.name(TIMESTAMP).value(timestamp.getTime());
			wr.name(LATITUDE).value(location.getLatitude());
			wr.name(LONGITUDE).value(location.getLongitude());
			wr.endObject();

			String content = output.toString();

			Log.d(TAG, "Message sent: "+content);

			byte[] sendData = content.getBytes();  // Default encoding is UTF-8

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destAddr, destPort);

			clientSocket.send(sendPacket);

			Log.d(TAG, "Sent packet!");

			
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Unknown host exception: ", e);

		} catch (IOException e) {
            throw new IllegalStateException("IO exception: ", e);
		}

	}

    @Override
    public void onDestroy() {
	    super.onDestroy();
	    if (clientSocket != null) {
            clientSocket.close();
        }
    }

}