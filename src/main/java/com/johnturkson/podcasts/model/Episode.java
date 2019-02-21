package com.johnturkson.podcasts.model;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Episode {
    private Podcast podcast;
    private String title;
    private String description;
    private URL source;
    
    private boolean downloaded;
    private boolean played;
    private boolean favorite;
    
    private Episode(Podcast podcast, String metadata) throws ParsingException {
        this.podcast = podcast;
        
        Matcher titleMatcher = Pattern
                .compile("(?s)<title>(?<title>.+?)</title>")
                .matcher(metadata);
        
        if (titleMatcher.find()) {
            title = titleMatcher.group("title").trim();
        } else {
            throw new ParsingException("Unable to parse episode title.");
        }
        
        Matcher descriptionMatcher = Pattern
                .compile("(?s)<description>\\s*(?:<!\\[CDATA\\[)?" +
                        "(?<description>.+?)\\s*(?:]]>)?\\s*</description>")
                .matcher(metadata);
        
        if (descriptionMatcher.find()) {
            description = descriptionMatcher.group("description").trim();
        } else {
            throw new ParsingException("Unable to parse episode description.");
        }
        
        Matcher sourceMatcher = Pattern.compile("(?s)(?:<enclosure url=\"|<source>)" +
                "(?<source>.+?)(?:\"|</source>)")
                .matcher(metadata);
        
        try {
            if (sourceMatcher.find()) {
                source = new URL(sourceMatcher.group("source").trim());
            } else {
                throw new ParsingException("Unable to parse episode source.");
            }
        } catch (MalformedURLException x) {
            throw new UncheckedIOException(x);
        }
        
        Matcher downloadedMatcher = Pattern
                .compile("<downloaded>(?<downloaded>.+?)</downloaded>")
                .matcher(metadata);
        if (downloadedMatcher.find()) {
            downloaded = Boolean.parseBoolean(downloadedMatcher.group("downloaded").trim());
        } else {
            downloaded = false;
        }
        
        Matcher playedMatcher = Pattern
                .compile("<played>(?<played>.+?)</played>")
                .matcher(metadata);
        if (playedMatcher.find()) {
            played = Boolean.parseBoolean(playedMatcher.group("played").trim());
        } else {
            played = false;
        }
        
        Matcher favoriteMatcher = Pattern
                .compile("<favorite>(?<favorite>.+?)</favorite>")
                .matcher(metadata);
        if (favoriteMatcher.find()) {
            favorite = Boolean.parseBoolean(favoriteMatcher.group("favorite").trim());
        } else {
            favorite = false;
        }
    }
    
    public static Episode parse(Podcast podcast, String metadata) throws ParsingException {
        return new Episode(podcast, metadata);
    }
    
    public void download() {
        // TODO download
        downloaded = true;
        Library.getInstance().getDownloadedEpisodes().add(this);
        podcast.export();
    }
    
    public void delete() {
        // TODO delete
        downloaded = false;
        Library.getInstance().getDownloadedEpisodes().remove(this);
        podcast.export();
    }
    
    public void favorite() {
        favorite = true;
        Library.getInstance().getFavoriteEpisodes().add(this);
        podcast.export();
    }
    
    public void unfavorite() {
        favorite = false;
        Library.getInstance().getFavoriteEpisodes().remove(this);
        podcast.export();
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Podcast getPodcast() {
        return podcast;
    }
    
    public URL getSource() {
        return source;
    }
    
    public boolean isDownloaded() {
        return downloaded;
    }
    
    public boolean isPlayed() {
        return played;
    }
    
    public boolean isFavorite() {
        return favorite;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return podcast.equals(episode.podcast) &&
                title.equals(episode.title);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(podcast, title);
    }
}
