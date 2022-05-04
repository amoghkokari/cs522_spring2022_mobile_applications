package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;

public interface Persistable {

    public void writeToProvider(ContentValues out);

}
