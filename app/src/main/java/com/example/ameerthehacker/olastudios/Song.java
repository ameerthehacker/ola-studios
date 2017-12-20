package com.example.ameerthehacker.olastudios;

/**
 * Created by ameerthehacker on 20/12/17.
 */

import java.util.*;

public class Song {

    private String name;
    private String url;
    private ArrayList<String> artists;
    private String coverImage;

    Song(String name, String url, ArrayList<String> artists, String coverImage) {
        this.name = name;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }
    public String getArtistsString() {
        String artists = "";
        for(int i = 0; i < this.artists.size(); i++) {
            if(i == this.artists.size() - 1) {
                artists += this.artists.get(i);
            }
            else {
                artists += this.artists.get(i) + ", ";
            }
        }
        return artists;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
