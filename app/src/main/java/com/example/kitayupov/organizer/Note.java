package com.example.kitayupov.organizer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class Note implements Parcelable {
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    private String body = "";
    private String type = "";
    private long date = System.currentTimeMillis();
    private int rating = 0;
    private boolean isDone = false;

    public Note() {

    }

    public Note(Parcel parcel) {
        this.body = parcel.readString();
        this.type = parcel.readString();
        this.date = parcel.readLong();
        this.rating = parcel.readInt();
        this.isDone = parcel.readInt() == 1;
    }

    public Note(String body, String type, long date, int rating) {
        this.body = body;
        this.type = type;
        this.date = date;
        this.rating = rating;
        isDone = false;
    }

    public Note(String body, String type, long date, int rating, boolean isDone) {
        this.body = body;
        this.type = type;
        this.date = date;
        this.rating = rating;
        this.isDone = isDone;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean done) {
        this.isDone = done;
    }

    @Override
    public String toString() {
        return "Name: " + body + ", Type: " + type +
                ", Date: " + DateFormat.format("dd.MM.yyyy", date) +
                ", Rating: " + rating + ", IsDone: " + isDone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(body);
        parcel.writeString(type);
        parcel.writeLong(date);
        parcel.writeInt(rating);
        parcel.writeInt(isDone ? 1 : 0);
    }
}
