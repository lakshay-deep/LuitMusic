package com.vianet.musicplayer.util;

import android.graphics.Bitmap;

public class MediaItem {
	private String title;
	private String artist;
	private String album;
	private String path;
	private long duration;
	private String albumId;
	private String composer;
    private Bitmap albumIdOffline;

	@Override
	public String toString() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
    public void setAlbumIdOffline(Bitmap albumId) {
        this.albumIdOffline = albumId;
    }

    public Bitmap getAlbumIdOffline() {
        return albumIdOffline;
    }

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}
}
