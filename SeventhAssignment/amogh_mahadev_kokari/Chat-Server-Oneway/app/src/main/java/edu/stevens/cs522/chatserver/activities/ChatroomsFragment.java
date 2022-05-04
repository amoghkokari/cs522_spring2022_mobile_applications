package edu.stevens.cs522.chatserver.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Chatroom;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.ui.TextAdapter;
import edu.stevens.cs522.chatserver.viewmodels.ChatViewModel;
import edu.stevens.cs522.chatserver.viewmodels.ChatroomViewModel;

public class ChatroomsFragment extends Fragment implements TextAdapter.OnItemClickListener<Chatroom> {

    @SuppressWarnings("unused")
    private final static String TAG = ChatroomsFragment.class.getCanonicalName();

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    public interface IChatroomListener {
        public void setChatroom(Chatroom chatroom);
    }

    private IChatroomListener listener;

    private ChatroomViewModel chatroomViewModel;

    private TextAdapter<Chatroom> chatroomsAdapter;

    /**
     * The current activated item position. Only used in landscape.
     */
    private int activatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
     */
    private RecyclerView chatroomList;

    public ChatroomsFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IChatroomListener) {
            listener = (IChatroomListener) context;
        } else {
            throw new IllegalStateException("Activity must implement INavigationListener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chatrooms, container, false);

        chatroomList = rootView.findViewById(R.id.chatroom_list);
        chatroomList.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // TODO Initialize the recyclerview and adapter for messages
        chatroomsAdapter = new TextAdapter<Chatroom>(chatroomList,this);
        chatroomList.setAdapter(chatroomsAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Called after onCreateView() returns.
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        // TODO initialize the chatroom view model
        chatroomViewModel = new ViewModelProvider(this).get(ChatroomViewModel.class);

        // TODO query the database asynchronously, and use messagesAdapter to display the result
        LiveData<List<Chatroom>> chatRooms = chatroomViewModel.fetchAllChatrooms();

        Observer<List<Chatroom>> observer = cht -> {
            chatroomsAdapter.setDataset(cht);
            chatroomList.setAdapter(chatroomsAdapter);
        };

        chatRooms.observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, Chatroom chatroom) {
        setActivatedPosition(position);
        // TODO ask the activity to respond to the selection (in single-pane layout, it will push detail fragment)
        listener.setChatroom(chatroom);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
        }
    }

    private void setActivatedPosition(int position) {

        if (position == ListView.INVALID_POSITION) {
            chatroomsAdapter.setItemChecked(activatedPosition);
        } else {
            chatroomsAdapter.setItemChecked(position);
        }
        activatedPosition = position;
    }

}