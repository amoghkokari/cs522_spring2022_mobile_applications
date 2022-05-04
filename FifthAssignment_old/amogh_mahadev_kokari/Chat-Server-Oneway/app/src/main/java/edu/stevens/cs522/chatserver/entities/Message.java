package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Created by dduggan.
 */

// TODO annotate (including FK constraints)
@Entity(foreignKeys = @ForeignKey(entity=Peer.class, onDelete=ForeignKey.CASCADE,
        parentColumns="id", childColumns="senderId"),indices = {@Index(value = {"senderId"})})
public class Message implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String chatRoom;

    public String messageText;

    public Date timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Parcel in) {
        // TODO
        id = in.readLong();
        chatRoom = in.readString();
        messageText = in.readString();
        timestamp = (Date) in.readSerializable();
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public String toString() {
        return sender + ": " + messageText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id);
        dest.writeString(chatRoom);
        dest.writeString(messageText);
        dest.writeSerializable(timestamp);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return new Message[size];
        }

    };

}

