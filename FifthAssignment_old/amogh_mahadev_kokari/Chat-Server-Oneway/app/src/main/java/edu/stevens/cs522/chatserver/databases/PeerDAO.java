package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Peer;

/*
 * TODO add annotations (NB insert should ignore conflicts, for upsert)
 *
 * We will continue to allow insertion to be done on main thread for noew.
 */
@Dao
public abstract class PeerDAO {

    /**
     * Get all peers in the database.
     * @return
     */
    @Query("SELECT * FROM peer")
    public abstract LiveData<List<Peer>> fetchAllPeers();

    /**
     * Get a single peer record (may be used in later assignments)
     * @param peerId
     * @return
     */
    @Query("SELECT * FROM peer WHERE id = :peerId")
    public abstract ListenableFuture<Peer> fetchPeer(long peerId);

    /**
     * Get the database primary key for a peer, based on chat name.
     * @param name
     * @return
     */
    @Query("Select id from Peer where name = :name")
    protected abstract long getPeerId(String name);

    /**
     *  Insert a peer and return their primary key (must not already be in database)
     * @param peer
     * @return
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract long insert(Peer peer);

    /**
     * Update the metadata for a peer (GPS coordinates, last seen)
     * @param peer
     */

    @Update
    protected abstract void update(Peer peer);

    @Transaction
    /**
     * TODO Add a peer record if it does not already exist;
     * update information if it is already defined.
     * This operation must be transactional, to avoid race condition
     * between search and insert
     */
    public long upsert(Peer peer) {
        // TODO
        long id = getPeerId(peer.name);
        if (id == 0) {
            id = insert(peer);
        } else {
            peer.id = id;
            update(peer);
        }
        return id;
    }
}
