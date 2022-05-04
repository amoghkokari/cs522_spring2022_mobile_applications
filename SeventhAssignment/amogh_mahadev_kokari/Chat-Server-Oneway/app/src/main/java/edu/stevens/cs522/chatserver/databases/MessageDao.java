package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Message;

// TODO add annotations for Repository pattern
@Dao
public interface MessageDao {

    @Query("select * from message WHERE message.chatroom = :chatroom")
    public abstract LiveData<List<Message>> fetchAllMessages(String chatroom);

    @Query("SELECT * FROM message  WHERE message.sender = :peerName")
    public LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void persist(Message message);

}
