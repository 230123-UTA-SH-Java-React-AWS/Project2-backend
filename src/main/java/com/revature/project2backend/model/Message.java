package com.revature.project2backend.model;

//A very bad way of handling messages to be sent by the websocket, that allows the message to be literally anything.
public class Message {
    private Object content;

    public Message() {
    }

    public Message(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Message content(Object content) {
        setContent(content);
        return this;
    }
}
