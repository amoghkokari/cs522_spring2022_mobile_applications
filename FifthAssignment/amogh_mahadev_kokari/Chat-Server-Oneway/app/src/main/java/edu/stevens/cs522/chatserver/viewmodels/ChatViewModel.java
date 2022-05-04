package edu.stevens.cs522.chatserver.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ChatViewModel extends AndroidViewModel {

    public static final String TAG = ChatViewModel.class.getCanonicalName();

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    public ChatViewModel(Application context) {
        super(context);
        chatDatabase = ChatDatabase.getInstance(context);
        Log.i(TAG, "Creating chat view model....");
    }

    public LiveData<List<Message>> fetchAllMessages() {
        if (messages == null) {
            messages = loadMessages();
        }
        return messages;
    }

    private LiveData<List<Message>> loadMessages() {
        return chatDatabase.messageDao().fetchAllMessages();
    }

    public void upsert(Peer peer) {
        chatDatabase.peerDao().upsert(peer);
    }

    public void persist(Message message) {
        chatDatabase.messageDao().persist(message);
    }

    @Override
    public void onCleared() {
        super.onCleared();
        Log.i(TAG, "Clearing chat view model....");
        chatDatabase = null;
    }
}
