package edu.stevens.cs522.chatserver.ui;

import edu.stevens.cs522.chatserver.entities.Message;

public class MessageSenderAdapter extends MessageAdapter {
    @Override
    public String getHeading(Message message) {
        return message.sender;
    }
}
