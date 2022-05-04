package edu.stevens.cs522.chatserver.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Used instead of ArrayAdapter in ViewPeersActivity and ViewPeeerActivity
 * since ArrayAdapter does not provide an operation for replacing the elements.
 */
public class SimpleArrayAdapter<T> extends CustomArrayAdapter<T> {

    public SimpleArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public void setFields(T element, View view) {
        TextView sender = view.findViewById(android.R.id.text1);
        sender.setText(element.toString());
    }

}
