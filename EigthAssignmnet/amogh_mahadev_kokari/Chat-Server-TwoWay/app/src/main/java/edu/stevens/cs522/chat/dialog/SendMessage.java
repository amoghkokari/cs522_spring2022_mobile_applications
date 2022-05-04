package edu.stevens.cs522.chat.dialog;

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

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Chatroom;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class SendMessage extends DialogFragment {

    private static final String TAG = SendMessage.class.getCanonicalName();

    public static final String CHATROOM_KEY = "chatroom";

    public interface IMessageSender {
        void send(String destinationHost,
                  int destinationPort,
                  String chatroom,
                  String chatname,
                  String text);
    }

    public static void launch(FragmentActivity activity, Chatroom chatroom, String tag) {
        SendMessage dialog = new SendMessage();
        Bundle args = new Bundle();
        args.putString(CHATROOM_KEY, chatroom.name);
        dialog.setArguments(args);
        dialog.show(activity.getSupportFragmentManager(), tag);
    }

    private IMessageSender listener;

    /*
     * Passed as an argument from the activity
     */
    private String chatroom;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

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

        chatroom = getArguments().getString(CHATROOM_KEY);
        if (chatroom == null) {
            throw new IllegalArgumentException("MIssing chatroom argument!");
        }

        // TODO initialize the UI.

        destinationHost = rootView.findViewById(R.id.destination_host);
        messageText = rootView.findViewById(R.id.message_text);
        destinationPort = rootView.findViewById(R.id.destination_port);

        // End todo

        Button confirm = rootView.findViewById(R.id.send);
        confirm.setOnClickListener(confirmListener);

        Button cancel = rootView.findViewById(R.id.cancel);
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

            if (isEmptyInput(destinationPort.getText())) {
                Log.d(TAG, "...missing destination port.");
                Toast.makeText(context, R.string.missing_destination_port, Toast.LENGTH_LONG).show();
                return;
            }

            if (isEmptyInput(messageText.getText())) {
                Log.d(TAG, "...missing message text.");
                Toast.makeText(context, R.string.missing_chat_text, Toast.LENGTH_LONG).show();
                return;
            }

            String destAddrString = destinationHost.getText().toString();
            int destPort = Integer.parseInt(destinationPort.getText().toString());
            String clientName = Settings.getSenderName(context);
            String message = messageText.getText().toString();
            Log.d(TAG, String.format("...sending \"%s\" to %s as %s....", message, chatroom, clientName));

            // TODO tell the activity to send the message

            listener.send(destAddrString, destPort, chatroom, clientName, message);

            Log.d(TAG, "...dismissing dialog.");
            SendMessage.this.dismiss();
        }
    };

    private final OnClickListener cancelListener = view -> SendMessage.this.getDialog().cancel();



}
