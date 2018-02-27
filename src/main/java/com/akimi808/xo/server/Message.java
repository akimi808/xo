package com.akimi808.xo.server;

/**
 * Created by akimi808 on 22/02/2018.
 */
public class Message {
    private boolean complete = false;
    private String text = "";

    public Message(boolean complete, String text) {

        this.setComplete(complete);
        this.setText(text);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (complete != message.complete) return false;
        return text.equals(message.text);
    }

    @Override
    public int hashCode() {
        int result = (complete ? 1 : 0);
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "complete=" + complete +
                ", text='" + text + '\'' +
                '}';
    }
}
