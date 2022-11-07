package com.example.yidianClock.model;

import android.net.Uri;
import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {
    String songName;
    String artist;
    Uri songsUri;
    int id;
    boolean isSelected;

    public Song(String songName, String artist, Uri songsUri) {
        this.songName = songName;
        this.artist = artist;
        this.songsUri = songsUri;
    }

    public Uri getSongsUri() {
        return songsUri;
    }

    public void setSongsUri(Uri songsUri) {
        this.songsUri = songsUri;
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
