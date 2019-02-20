package com.johnturkson.podcasts.model;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Episode {
    private Podcast podcast;
    private String title;
    private String description;
    private URL source;
    
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
        
        Matcher sourceMatcher = Pattern.compile("(?s)<enclosure url=\"(?<source>[^\"]+)\"")
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
    }
    
    public static Episode parse(Podcast podcast, String metadata) throws ParsingException {
        return new Episode(podcast, metadata);
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
}
