package com.johnturkson.podcasts.model;

import com.johnturkson.podcasts.io.FileReader;
import com.johnturkson.podcasts.io.URLReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Podcast {
    private URL feed;
    private String title;
    private String description;
    private String author;
    private String copyright;
    private URL website;
    private URL artwork;
    private List<Episode> episodes;
    
    private boolean subscribed;
    private boolean favorite;
    
    private Podcast(String metadata) throws ParsingException {
        Matcher feedMatcher = Pattern
                .compile("<feed>(?<feed>.+?)</feed>")
                .matcher(metadata);
        
        if (feedMatcher.find()) {
            try {
                feed = new URL(feedMatcher.group("feed"));
            } catch (MalformedURLException x) {
                throw new UncheckedIOException(x);
            }
        }
        
        Matcher titleMatcher = Pattern
                .compile("(?s)<title>(?<title>.+?)</title>")
                .matcher(metadata);
        
        if (titleMatcher.find()) {
            title = titleMatcher.group("title").trim();
        } else {
            throw new ParsingException("Unable to parse podcast title");
        }
        
        Matcher descriptionMatcher = Pattern
                .compile("(?s)<description>\\s*(?:<!\\[CDATA\\[)?(?<description>.+?)\\s*(?:]]>)?\\s*</description>")
                .matcher(metadata);
        
        if (descriptionMatcher.find()) {
            description = descriptionMatcher.group("description").trim();
        } else {
            throw new ParsingException("Unable to parse podcast description.");
        }
        
        Matcher authorMatcher = Pattern
                .compile("(?s)(?:<itunes:author>|<author>)(?<author>.+?)" +
                        "(?:</itunes:author>|</author>)")
                .matcher(metadata);
        
        if (authorMatcher.find()) {
            author = authorMatcher.group("author").trim();
        } else {
            throw new ParsingException("Unable to parse podcast author.");
        }
        
        Matcher copyrightMatcher = Pattern
                .compile("(?s)<copyright>(?<copyright>.+?)</copyright>")
                .matcher(metadata);
        
        if (copyrightMatcher.find()) {
            copyright = copyrightMatcher.group("copyright").trim();
        } else {
            throw new ParsingException("Unable to parse podcast copyright.");
        }
        
        Matcher websiteMatcher = Pattern
                .compile("(?s)(?:<link>|<website>)(?<website>.+?)(?:</link>|</website>)")
                .matcher(metadata);
        
        try {
            if (websiteMatcher.find()) {
                website = new URL(websiteMatcher.group("website").trim());
            } else {
                throw new ParsingException("Unable to parse podcast website.");
            }
        } catch (MalformedURLException x) {
            throw new UncheckedIOException(x);
        }
        
        Matcher artworkMatcher = Pattern
                .compile("(?s)(?:<itunes:image href=\"|<artwork>)(?<artwork>.+?)" +
                        "(?:\"\\s*/>|</artwork>)")
                .matcher(metadata);
        
        try {
            if (artworkMatcher.find()) {
                artwork = new URL(artworkMatcher.group("artwork").trim());
            } else {
                throw new ParsingException("Unable to parse podcast artwork.");
            }
            
        } catch (MalformedURLException x) {
            throw new UncheckedIOException(x);
        }
        
        Matcher subscribedMatcher = Pattern
                .compile("<subscribed>(?<subscribed>.+?)</subscribed>")
                .matcher(metadata);
        if (subscribedMatcher.find()) {
            subscribed = Boolean.parseBoolean(subscribedMatcher.group("subscribed").trim());
        } else {
            subscribed = false;
        }
        
        Matcher favoriteMatcher = Pattern
                .compile("<favorite>(?<favorite>.+?)</favorite>")
                .matcher(metadata);
        if (favoriteMatcher.find()) {
            favorite = Boolean.parseBoolean(favoriteMatcher.group("favorite").trim());
        } else {
            favorite = false;
        }
        
        episodes = new ArrayList<>();
        Matcher episodeMatcher = Pattern.compile("(?s)(?:<item>|<episode>)(?<episode>.+?)" +
                "(?:</item>|</episode>)")
                .matcher(metadata);
        
        while (episodeMatcher.find()) {
            episodes.add(Episode.parse(this, episodeMatcher.group("episode").trim()));
        }
    }
    
    private Podcast(URL feed) throws ParsingException {
        this(new URLReader(feed).read());
        this.feed = feed;
    }
    
    private Podcast(Path saved) throws ParsingException {
        this(new FileReader(saved).read());
    }
    
    public static Podcast parse(URL feed) throws ParsingException {
        return new Podcast(feed);
    }
    
    public static Podcast parse(Path saved) throws ParsingException {
        return new Podcast(saved);
    }
    
    public URL getFeed() {
        return feed;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getCopyright() {
        return copyright;
    }
    
    public URL getWebsite() {
        return website;
    }
    
    public URL getArtwork() {
        return artwork;
    }
    
    public List<Episode> getEpisodes() {
        return episodes;
    }
    
    public boolean isSubscribed() {
        return subscribed;
    }
    
    public boolean isFavorite() {
        return favorite;
    }
    
    public void subscribe() {
        subscribed = true;
        Library.getInstance().getSubscribedPodcasts().add(this);
        export();
    }
    
    public void unsubscribe() {
        subscribed = false;
        Library.getInstance().getSubscribedPodcasts().remove(this);
        export();
    }
    
    public void favorite() {
        favorite = true;
        Library.getInstance().getFavoritePodcasts().add(this);
        export();
    }
    
    public void unfavorite() {
        favorite = false;
        Library.getInstance().getFavoritePodcasts().remove(this);
        export();
    }
    
    public void export() {
        String[] disallowedCharacters = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*", "."};
        String validTitle = title;
        for (String s : disallowedCharacters) {
            validTitle = validTitle.replaceAll(Pattern.quote(s), "");
        }
        
        if (Files.notExists(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"))) {
            try {
                Files.createDirectories(Library.DEFAULT_LOCATION.resolve(validTitle));
                Files.createFile(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"));
            } catch (IOException x) {
                throw new UncheckedIOException(x);
            }
        }
        
        StringBuilder formatted = new StringBuilder();
        
        formatted.append("<podcast>\n");
        formatted.append("\t<title>").append(title).append("</title>\n");
        formatted.append("\t<feed>").append(feed).append("</feed>\n");
        formatted.append("\t<description>").append(description).append("</description>\n");
        formatted.append("\t<author>").append(author).append("</author>\n");
        formatted.append("\t<copyright>").append(copyright).append("</copyright>\n");
        formatted.append("\t<website>").append(website).append("</website>\n");
        formatted.append("\t<artwork>").append(artwork).append("</artwork>\n");
        formatted.append("\t<subscribed>").append(subscribed).append("</subscribed>\n");
        formatted.append("\t<favorite>").append(favorite).append("</favorite>\n");
        
        for (Episode episode : episodes) {
            formatted.append("\t<episode>\n");
            formatted.append("\t\t<title>").append(episode.getTitle()).append("</title>\n");
            formatted.append("\t\t<description>").append(episode.getDescription()).append("</description>\n");
            formatted.append("\t\t<source>").append(episode.getSource()).append("</source>\n");
            formatted.append("\t\t<downloaded>").append(episode.isDownloaded()).append("</downloaded>\n");
            formatted.append("\t\t<played>").append(episode.isPlayed()).append("</played>\n");
            formatted.append("\t\t<favorite>").append(episode.isFavorite()).append("</favorite>\n");
            formatted.append("\t</episode>\n");
        }
        
        formatted.append("</podcast>\n");
        
        try {
            Files.writeString(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"),
                    formatted.toString());
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
    
    @Override
    public String toString() {
        return title + "\n" + description + "\n" + author + "\n" + copyright;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Podcast podcast = (Podcast) o;
        return title.equals(podcast.title);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
