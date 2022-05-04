package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;

/**
 * Created by dduggan.
 */

/*
 * TODO as an entity object
 *
 * You should specify that "sender" is a FK reference to a peer record.
 * You should also declare a (non-unique) index for this FK field, so
 * integrity checking does not involve a linear search of this table.
 */
@Entity(foreignKeys = @ForeignKey(entity=Peer.class, onDelete=ForeignKey.CASCADE,
        parentColumns="name", childColumns="sender"),indices = {@Index(value = {"sender"})})
public class Message implements Parcelable {

    // TODO primary key

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String chatroom;

    public String messageText;

    // TODO Last time we heard from this peer.

    @TypeConverters(DateConverter.class)
    public Date timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public Message() {
    }

    public String toString() {
        return messageText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Message(Parcel in) {
        id = in.readLong();
        chatroom = in.readString();
        messageText = in.readString();
        timestamp = DateUtils.readDate(in);
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(chatroom);
        out.writeString(messageText);
        DateUtils.writeDate(out, timestamp);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(sender);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}

