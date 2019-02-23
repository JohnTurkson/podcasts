package com.johnturkson.podcasts.model;

import com.johnturkson.podcasts.tools.EpisodeDeleter;
import com.johnturkson.podcasts.tools.EpisodeDownloader;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Episode {
    private Podcast podcast;
    private String title;
    private String description;
    private URL source;
    private int length;
    private boolean explicit;
    private boolean downloaded;
    private boolean favorite;
    private Status status;
    private int progress;
    
    private Episode(Podcast podcast, String metadata) throws ParsingException {
        this.podcast = podcast;
        
        Matcher titleMatcher = Pattern
                .compile("(?s)<title>(?<title>.*?)</title>")
                .matcher(metadata);
        
        if (titleMatcher.find()) {
            title = titleMatcher.group("title").trim();
        } else {
            throw new ParsingException("Unable to parse episode title.");
        }
        
        Matcher descriptionMatcher = Pattern
                .compile("(?s)<description>\\s*(?:<!\\[CDATA\\[)?" +
                        "(?<description>.*?)\\s*(?:]]>)?\\s*</description>")
                .matcher(metadata);
        
        if (descriptionMatcher.find()) {
            description = descriptionMatcher.group("description").trim();
        } else {
            throw new ParsingException("Unable to parse episode description.");
        }
        
        Matcher sourceMatcher = Pattern.compile("(?s)(?:<enclosure url=\"|<source>)" +
                "(?<source>.*?)(?:\"|</source>)")
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
        
        Matcher lengthMatcher = Pattern.compile("(?:<length>|<itunes:duration>)" +
                "(?:(?<seconds>\\d+)|(?<combined>(?:\\d+:)*\\d+))" +
                "(?:</length>|</itunes:duration>)").matcher(metadata);
        
        if (lengthMatcher.find()) {
            if (lengthMatcher.group("seconds") != null) {
                length = Integer.parseInt(lengthMatcher.group("seconds"));
            } else {
                String[] fields = lengthMatcher.group("combined").split(":");
                length = 0;
                for (int i = 0; i < fields.length; i++) {
                    int seconds = Integer.parseInt(fields[i]);
                    for (int j = 0; j < fields.length - i - 1; j++) {
                        seconds *= 60;
                    }
                    length += seconds;
                }
            }
        } else {
            throw new ParsingException("Unable to determine episode length.");
        }
        
        Matcher explicitMatcher = Pattern.compile("(?s)<(?:itunes:)?explicit>" +
                "(?<explicit>yes|no|true|false)" +
                "</(?:itunes:)?explicit>").matcher(metadata);
        
        if (explicitMatcher.find()) {
            explicit = explicitMatcher.group("explicit").equals("yes") ||
                    explicitMatcher.group("explicit").equals("true");
        } else {
            explicit = true;
        }
        
        Matcher downloadedMatcher = Pattern
                .compile("(?s)<downloaded>(?<downloaded>yes|no|true|false)</downloaded>")
                .matcher(metadata);
        if (downloadedMatcher.find()) {
            downloaded = downloadedMatcher.group("downloaded").equals("yes") ||
                    downloadedMatcher.group("downloaded").equals("true");
        } else {
            downloaded = false;
        }
        
        Matcher favoriteMatcher = Pattern
                .compile("(?s)<favorite>(?<favorite>yes|no|true|false)</favorite>")
                .matcher(metadata);
        if (favoriteMatcher.find()) {
            favorite = favoriteMatcher.group("favorite").equals("yes") ||
                    favoriteMatcher.group("favorite").equals("true");
        } else {
            favorite = false;
        }
        
        Matcher statusMatcher = Pattern.compile("(?s)<status>" +
                "(?<status>UNPLAYED|UNFINISHED|COMPLETED)</status>").matcher(metadata);
        
        if (statusMatcher.find()) {
            status = Status.valueOf(statusMatcher.group("status"));
        } else {
            status = Status.UNPLAYED;
        }
        
        Matcher progressMatcher = Pattern.compile("(?s)<progress>(?<progress>\\d+)</progress>")
                .matcher(metadata);
        if (progressMatcher.find()) {
            progress = Integer.parseInt(progressMatcher.group("progress"));
        } else {
            progress = 0;
        }
        
    }
    
    public static Episode parse(Podcast podcast, String metadata) throws ParsingException {
        return new Episode(podcast, metadata);
    }
    
    public void download() {
        downloaded = true;
        EpisodeDownloader.download(this);
        Library.getInstance().getDownloadedEpisodes().add(this);
        podcast.export();
    }
    
    public void delete() {
        downloaded = false;
        EpisodeDeleter.delete(this);
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
    
    public Path getEpisodeLocation() {
        // TODO better solution (extract into method?)
        Path location = Library.DEFAULT_LOCATION;
        String[] disallowedCharacters = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*", "."};
        String subDir = podcast.getTitle();
        for (String s : disallowedCharacters) {
            subDir = subDir.replaceAll(Pattern.quote(s), "");
        }
        location = location.resolve(subDir);
        subDir = title;
        for (String s : disallowedCharacters) {
            subDir = subDir.replaceAll(Pattern.quote(s), "");
        }
        location = location.resolve(subDir + ".mp3");
    
        return location;
    }
    
    public Podcast getPodcast() {
        return podcast;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public URL getSource() {
        return source;
    }
    
    public int getLength() {
        return length;
    }
    
    public boolean isExplicit() {
        return explicit;
    }
    
    public boolean isDownloaded() {
        return downloaded;
    }
    
    public boolean isFavorite() {
        return favorite;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public int getProgress() {
        return progress;
    }
    
    @Override
    public String toString() {
        return title + "\n" + description;
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
    
    public enum Status {
        UNPLAYED,
        UNFINISHED,
        COMPLETED
    }
}
