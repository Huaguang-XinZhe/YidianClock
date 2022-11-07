package com.example.yidianClock.model;

import android.net.Uri;
import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {
    String songName;
    String artist;
    int id;
    boolean isSelected;

    public Song(String songName, String artist, int id) {
        this.songName = songName;
        this.artist = artist;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
