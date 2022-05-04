package edu.stevens.cs522.chatserver.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TextAdapter<T> extends RecyclerView.Adapter<TextAdapter.ViewHolder> {

    private List<T> dataset;

    private final RecyclerView recyclerView;

    private final OnItemClickListener listener;

    /**
     * Initialize the dataset of the Adapter
     */
    public TextAdapter(RecyclerView recyclerView) {
        this(recyclerView, null);
    }

    public TextAdapter(RecyclerView recyclerView, OnItemClickListener listener) {
        this.dataset = new ArrayList<>();
        this.recyclerView = recyclerView;
        this.listener = listener;
    }

    /*
     * The big challenge with RecyclerView is how to add an item click listener.
     *
     * We define our own version of OnItemClickListener that does not require id.
     */
    public interface OnItemClickListener {
        public void onItemClick(RecyclerView parent, View row, int position);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textView;

        private final OnItemClickListener listener;

        private final RecyclerView recyclerView;

        public ViewHolder(View view, OnItemClickListener listener, RecyclerView recyclerView) {
            super(view);

            this.textView = (TextView) view.findViewById(android.R.id.text1);

            this.listener = listener;
            view.setOnClickListener(this);

            this.recyclerView = recyclerView;
        }

        public void setText(String text) {
            textView.setText(text);
        }

        @Override
        public void onClick(View v) {
            int position = this.getBindingAdapterPosition();
            // TODO invoke the listener
            listener.onItemClick(recyclerView, v, position);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);

        return new ViewHolder(view, listener, recyclerView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Do not bind the position of the view holder here, it will not be updated
    // if the contents of the backing store are edited (deletions & insertions)
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

    public T getItem(int position) {
        return dataset.get(position);
    }

}

