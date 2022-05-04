package edu.stevens.cs522.chatserver.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import  edu.stevens.cs522.chatserver.databases.MessageDAO;

public class PeerViewModel extends AndroidViewModel {

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    private Peer currentPeer;

    public PeerViewModel(Application context) {
        super(context);
        chatDatabase = ChatDatabase.getInstance(context);
    }

    // TODO finish this

    public LiveData<List<Message>> fetchMessagesFromPeer(Peer peer) {
        if (messages == null || (currentPeer !=null && currentPeer.id != peer.id)) {
            messages = loadMessages(peer);
        }
        return messages;
    }

    private LiveData<List<Message>> loadMessages(Peer peer) {
        currentPeer = peer;
        return chatDatabase.messageDao().fetchMessagesFromPeer(currentPeer.id);
    }

    @Override
    public void onCleared() {
        super.onCleared();
        chatDatabase = null;
    }
}
