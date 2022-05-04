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
public interface MessageDAO {

    @Query("select * from message")
    public LiveData<List<Message>> fetchAllMessages();

    @Query("SELECT * FROM message WHERE message.senderId = :peerId")
    public LiveData<List<Message>> fetchMessagesFromPeer(long peerId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void persist(Message message);

}
