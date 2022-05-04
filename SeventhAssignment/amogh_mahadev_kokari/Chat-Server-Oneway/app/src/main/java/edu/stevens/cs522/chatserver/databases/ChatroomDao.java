package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Chatroom;
import edu.stevens.cs522.chatserver.entities.Peer;

@Dao
/*
 * Make sure to declare an index on chatroom name, that specifies chat names are unique.
 */
public abstract class ChatroomDao {

    @Query("SELECT * FROM Chatroom")
    public abstract LiveData<List<Chatroom>> fetchAllChatrooms();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Chatroom chatroom);
}
