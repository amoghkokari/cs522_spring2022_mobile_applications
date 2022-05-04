package edu.stevens.cs522.chatserver.entities;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;

/**
 * Created by dduggan.
 */
@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Peer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    // Last time we heard from this peer.
//    @TypeConverters(DateConverter.class)
    public Date timestamp;

    // Where we heard from them
    public Double latitude;

    public Double longitude;

    // Where they sent message from
    @TypeConverters(InetAddressConverter.class)
    public InetAddress address;

    public int port;

    @Override
    public String toString() {
        return name;
    }

    public Peer() {
    }

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = (Date) in.readSerializable();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = (InetAddress) in.readSerializable();
        port = in.readInt();
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeSerializable(timestamp);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeSerializable(address);
        out.writeInt(port);
        // TODO
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            // TODO
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            // TODO
            return new Peer[size];
        }

    };
}
