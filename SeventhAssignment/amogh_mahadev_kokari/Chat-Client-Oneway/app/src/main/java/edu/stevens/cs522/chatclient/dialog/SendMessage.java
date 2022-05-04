package edu.stevens.cs522.chatclient.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.io.StringWriter;
import java.net.DatagramPacket;

import edu.stevens.cs522.chatclient.R;
import edu.stevens.cs522.chatclient.activities.ChatClient;

/**
 * Created by dduggan.
 */

public class SendMessage extends DialogFragment {

    private static final String TAG = SendMessage.class.getCanonicalName();

    public interface IMessageSender {
        public void send(String destinationhost, String chatroom, String chatname, String text);
    }

    public static final String CHATROOM_KEY = "chatroom";

    public static void launch(FragmentActivity activity, String tag) {
        SendMessage dialog = new SendMessage();
        dialog.show(activity.getSupportFragmentManager(), tag);
    }

    private IMessageSender listener;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText chatroom;

    private EditText chatName;

    private EditText messageText;

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        if (!(activity instanceof IMessageSender)) {
            throw new IllegalStateException("Activity must implement IMessageSender.");
        }
        listener = (IMessageSender) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If not using AlertDialog
        View rootView = inflater.inflate(R.layout.send_message, container, false);

        // TODO initialize the UI.

        destinationHost = rootView.findViewById(R.id.destination_host);
        chatroom = rootView.findViewById(R.id.chat_room);
        chatName = rootView.findViewById(R.id.chat_name);
        messageText = rootView.findViewById(R.id.message_text);

        // End todo

        Button confirm = (Button) rootView.findViewById(R.id.send);
        confirm.setOnClickListener(confirmListener);

        Button cancel = (Button) rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Not much to do unless using AlertDialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /*
     * This should be in StringUtils.
     */
    private static boolean isEmptyInput(Editable text) {
        return text.toString().trim().length() == 0;
    }

    private final OnClickListener confirmListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Confirming message send...");
            Context context = requireActivity();

            if (isEmptyInput(destinationHost.getText())) {
                Log.d(TAG, "...missing destination host.");
                Toast.makeText(context, R.string.missing_destination_host, Toast.LENGTH_LONG).show();
                return;
            }

            if (isEmptyInput(chatroom.getText())) {
                Log.d(TAG, "...missing chat room.");
                Toast.makeText(context, R.string.missing_chat_room, Toast.LENGTH_LONG).show();
            }

            if (isEmptyInput(chatName.getText())) {
                Log.d(TAG, "...missing chat name.");
                Toast.makeText(context, R.string.missing_chat_name, Toast.LENGTH_LONG).show();
            }

            if (isEmptyInput(messageText.getText())) {
                Log.d(TAG, "...missing message text.");
                Toast.makeText(context, R.string.missing_chat_text, Toast.LENGTH_LONG).show();
                return;
            }

            String destAddrString = destinationHost.getText().toString();
            String chatroomName = chatroom.getText().toString();
            String clientName = chatName.getText().toString();
            String message = messageText.getText().toString();
            Log.d(TAG, String.format("...sending \"%s\" to %s as %s....", message, chatroomName, clientName));

            // TODO tell the activity to send the message

            listener.send(destAddrString, chatroomName, clientName, message);

            Log.d(TAG, "...dismissing dialog.");
            SendMessage.this.dismiss();
        }
    };

    private final OnClickListener cancelListener = new OnClickListener() {
        public void onClick(View view) {
            SendMessage.this.getDialog().cancel();
        }
    };



}
