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

    /**
     * Get all messages in the database.
     * @return
     */

    @Query("select * from message")
    public LiveData<List<Message>> fetchAllMessages();

    /**
     * Get all the messages for a particular peer.
     * @param peerName
     * @return
     */

    @Query("SELECT * FROM message WHERE message.sender = :peerName")
    public LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    /**
     * Add a new message to the database.
     * @param message
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void persist(Message message);

}
