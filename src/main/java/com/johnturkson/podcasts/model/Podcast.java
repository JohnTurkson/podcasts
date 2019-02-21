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
    
    public void export() throws IOException {
        String[] disallowedCharacters = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*", "."};
        String validTitle = title;
        for (String s : disallowedCharacters) {
            validTitle = validTitle.replaceAll(Pattern.quote(s), "");
        }
        
        if (Files.notExists(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"))) {
            Files.createDirectories(Library.DEFAULT_LOCATION.resolve(validTitle));
            Files.createFile(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"));
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
        
        for (Episode episode : episodes) {
            formatted.append("\t<episode>\n");
            formatted.append("\t\t<title>").append(episode.getTitle()).append("</title>\n");
            formatted.append("\t\t<description>").append(episode.getDescription()).append("</description>\n");
            formatted.append("\t\t<source>").append(episode.getSource()).append("</source>\n");
            formatted.append("\t</episode>\n");
        }
        
        formatted.append("</podcast>\n");
        
        Files.writeString(Library.DEFAULT_LOCATION.resolve(validTitle).resolve("metadata.xml"),
                formatted.toString());
    }
}
