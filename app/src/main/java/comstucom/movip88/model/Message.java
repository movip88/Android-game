package comstucom.movip88.model;

import com.google.gson.annotations.SerializedName;

/**
 * Clase serializable para gestinoar los mesages de un usuario con ostro usuario
 */
public class Message {

    @SerializedName("Id")
    private int id;

    @SerializedName("FromId")
    private int fromId;

    @SerializedName("ToId")
    private int toId;

    @SerializedName("Text")
    private String text;

    @SerializedName("SentAt")
    private String sentAt;

    @SerializedName("ReceivedAt")
    private String receivedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }
}
