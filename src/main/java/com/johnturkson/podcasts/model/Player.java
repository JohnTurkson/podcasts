package com.johnturkson.podcasts.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private static Player INSTANCE = new Player();
    private List<Episode> queue = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    
    public static Player getInstance() {
        return INSTANCE;
    }
    
    public List<Episode> getQueue() {
        return queue;
    }
    
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public void add(Episode episode) {
        queue.add(episode);
    }
    
    public void play(Episode episode) {
        // TODO if episode is the same as currently playing
        
        // prevent "double" playing
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        queue.add(0, episode);
        
        mediaPlayer = new MediaPlayer(getMedia(episode));
        mediaPlayer.play();
        episode.setStatus(Episode.Status.PLAYING);
        mediaPlayer.currentTimeProperty().addListener(observable -> {
            if (episode.getProgress() != getCurrentTime() && getCurrentTime() != 0) {
                episode.setStatus(Episode.Status.PLAYING);
                episode.setProgress(getCurrentTime());
                episode.getPodcast().export();
            }
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            episode.setStatus(Episode.Status.PLAYED);
            episode.getPodcast().export();
            stop();
            if (queue.isEmpty()) {
                // TODO disable player?
            } else {
                queue.remove(0);
                // TODO move to next episode
            }
        });
        play();
    }
    
    public void export() {
        // TODO reloading episode for next time
    }
    
    public void queue(Episode episode) {
        queue.add(episode);
    }
    
    public void play() {
        mediaPlayer.play();
    }
    
    public void pause() {
        mediaPlayer.pause();
    }
    
    public void stop() {
        mediaPlayer.stop();
    }
    
    public void next() {
        
    }
    
    public void previous() {
        
    }
    
    public void seek(int position) {
        mediaPlayer.seek(new Duration(position * 1000));
    }
    
    public int getCurrentTime() {
        return (int) mediaPlayer.getCurrentTime().toSeconds();
    }
    
    public int getTotalTime() {
        return (int) mediaPlayer.getTotalDuration().toSeconds();
    }
    
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    public boolean isPaused() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED;
    }
    
    public boolean isStopped() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED;
    }
    
    private Media getMedia(Episode episode) {
        return new Media(episode.getEpisodeLocation().toUri().toASCIIString());
    }
    
    public void restore() {
        // TODO
    }
}
