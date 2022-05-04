package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chatserver.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String chatroom;

    public String messageText;

    public Date timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public Message() {
    }

    public Message(Cursor in) {
        id = MessageContract.getId(in);
        chatroom = MessageContract.getChatroom(in);
        messageText = MessageContract.getMessageText(in);
        timestamp = MessageContract.getTimestamp(in);
        latitude = MessageContract.getLatitude(in);
        longitude = MessageContract.getLongitude(in);
        sender = MessageContract.getSender(in);
    }

    @Override
    public void writeToProvider(ContentValues out) {
        MessageContract.putId(out, id);
        MessageContract.putChatroom(out, chatroom);
        MessageContract.putMessageText(out, messageText);
        MessageContract.putTimestamp(out, timestamp);
        MessageContract.putLatitude(out, latitude);
        MessageContract.putLongitude(out, longitude);
        MessageContract.putSender(out, sender);
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

