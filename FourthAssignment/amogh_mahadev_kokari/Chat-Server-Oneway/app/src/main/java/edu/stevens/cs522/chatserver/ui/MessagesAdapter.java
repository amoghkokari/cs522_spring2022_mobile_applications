package edu.stevens.cs522.chatserver.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;

/**
 *  This is for ChatServerActivity, since ArrayAdapter assumes a single TexxtView
 *  in the row layout and sets it by calling toString() on the corresponding element.
 */
public class MessagesAdapter extends CustomArrayAdapter<Message> {

    public MessagesAdapter(Context context) { super(context, R.layout.message); }

    @Override
    public void setFields(Message message, View view) {
        TextView sender = view.findViewById(R.id.header);
        sender.setText(message.sender);
        TextView text = view.findViewById(R.id.message);
        text.setText(message.messageText);
    }

}
