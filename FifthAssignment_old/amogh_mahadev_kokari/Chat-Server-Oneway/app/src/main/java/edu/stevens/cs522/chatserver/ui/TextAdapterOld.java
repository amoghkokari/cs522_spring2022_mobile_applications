package edu.stevens.cs522.chatserver.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TextAdapterOld<T> extends RecyclerView.Adapter<TextAdapterOld.ViewHolder> implements RecyclerView.OnChildAttachStateChangeListener, View.OnClickListener {

    private List<T> dataset;

    private final RecyclerView recyclerView;

    private OnItemClickListener listener;

    /**
     * Initialize the dataset of the Adapter
     */
    public TextAdapterOld(RecyclerView recyclerView) {
        this(recyclerView, null);
    }

    public TextAdapterOld(RecyclerView recyclerView, OnItemClickListener listener) {
        this.dataset = new ArrayList<>();
        this.recyclerView = recyclerView;
        this.listener = listener;
        recyclerView.addOnChildAttachStateChangeListener(this);
    }

    /*
     * The big challenge with RecyclerView is how to add an item click listener.
     * Typically people do this in onBindViewHolder, but that means allocating a new
     * intermediate listener every time a row is scrolled in the recyclerview, or in
     * onCreateViewHolder, but that means allocating a new
     * intermediate listener every time a row is added to the recyclerview.
     * Better to define this intermediate callback (implementing OnClick) once,
     * and register this with each row as it is added.  RecyclerView provides the
     * OnChildAttachedStateChangeListener interface to let us know when rows are added.
     * From https://stackoverflow.com/a/35917564
     */

    /*
     * We define our own version of OnItemClickListener that does not require id.
     */
    public interface OnItemClickListener {
        public void onItemClick(RecyclerView parent, View row, int position);
    }

    /*
     * This is where we learn of addition of child views (rows)
     */
    @Override
    public void onChildViewAttachedToWindow(@NonNull View row) {
        if (listener != null) {
            row.setOnClickListener(this);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View row) {
    }

    /*
     * Now comes the trick: if we define one intermediate OnClickListener callback,
     * that is invoked when any row is clicked, where do we get the position of the row from,
     * needed for OnItemClickListener?
     */
    @Override
    public void onClick(View row) {
        if (listener != null) {
            RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(row);
            int position = childViewHolder.getBindingAdapterPosition();

            // TODO invoke the item click listener on this row.
            listener.onItemClick(recyclerView, row, position);
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        private OnItemClickListener listener;

        public ViewHolder(View view) {
            super(view);

            this.textView = (TextView) view.findViewById(android.R.id.text1);

        }

        public void setText(String text) {
            textView.setText(text);
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Not a good idea to register a new callback on clicks for every binding of a row
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        T data = dataset.get(position);
        viewHolder.setText(data.toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /*
     * Invoked by live data observer.
     */
    public void setDataset(List<T> dataset) {
        this.dataset = dataset;
    }
}

