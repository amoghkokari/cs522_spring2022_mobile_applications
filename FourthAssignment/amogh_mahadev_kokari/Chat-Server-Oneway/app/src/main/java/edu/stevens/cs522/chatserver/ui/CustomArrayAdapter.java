package edu.stevens.cs522.chatserver.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chatserver.entities.Message;

/**
 * Implements a simple array adapter that allows the backing store
 * to be updated when the database is re-queried (after an update).
 *
 * This is customized further for two-line and single-line row layouts.
 */
public abstract class CustomArrayAdapter<T> extends BaseAdapter {

    private List<T> elements = new ArrayList<T>();

    private final int layout;

    private final LayoutInflater inflater;

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public T getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    /*
     * This is the key method that builds the view for a row in the listview.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        /*
         * Reuse the convertView if provided, otherwise inflate a new row.
         */
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(layout, parent, false);
        }

        /*
         * Now set the fields in the layout
         */
        setFields(getItem(position), view);

        return view;
    }

    public CustomArrayAdapter(Context context, int layout) {
        this.inflater = LayoutInflater.from(context);
        this.layout = layout;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    /*
     * A subclass specifies how to set the fields in the view for a row.
     */
    public abstract void setFields(T element, View view);
}
