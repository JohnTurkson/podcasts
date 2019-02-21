package com.johnturkson.podcasts.search;

import com.johnturkson.podcasts.io.URLReader;
import com.johnturkson.podcasts.model.ParsingException;
import com.johnturkson.podcasts.model.Podcast;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PodcastSearcher {
    private static final int DEFAULT_LIMIT = 10;
    
    private int limit;
    
    public PodcastSearcher() {
        this.limit = DEFAULT_LIMIT;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public List<Podcast> search(String query) {
        URL request = generateSearchURL(query);
        String response = getResponse(request);
        return parseResponse(response);
    }
    
    private URL generateSearchURL(String query) {
        try {
            return new URL("https://itunes.apple.com/search?" +
                    "&term=" + replaceUnsafeCharacters(query) +
                    "&entity=" + "podcast" +
                    "&limit=" + limit);
        } catch (MalformedURLException x) {
            // ignored as URL is valid
            throw new UncheckedIOException(x);
        }
    }
    
    private String getResponse(URL request) {
        return new URLReader(request).read();
    }
    
    private List<Podcast> parseResponse(String response) {
        List<Podcast> podcasts = new ArrayList<>();
        
        List<URL> feeds = new ArrayList<>();
        
        Pattern feedPattern = Pattern.compile("\"feedUrl\":\"(?<feed>[^\"]+)\"");
        Matcher feedMatcher = feedPattern.matcher(response);
        
        while (feedMatcher.find()) {
            try {
                feeds.add(new URL(feedMatcher.group("feed")));
            } catch (MalformedURLException x) {
                // ignored - assume response URL is valid
                throw new UncheckedIOException(x);
            }
        }
        
        for (URL feed : feeds) {
            try {
                podcasts.add(Podcast.parse(feed));
            } catch (ParsingException e) {
                System.out.println("Unable to parse one or more podcasts.");
                e.printStackTrace();
            }
        }
        
        return podcasts;
    }
    
    /**
     * Replaces unsafe characters in a URL string by escaping them with followed by the ASCII
     * hexadecimal code corresponding to the given character. See RFC 1738 for more details.
     *
     * @param query the string to generate a URL from
     * @return a new String with all unsafe characters escaped
     */
    private String replaceUnsafeCharacters(String query) {
        Map<String, String> replacements = new LinkedHashMap<>();
        // LinkedHashMap used so "%" is escaped first
        replacements.put("%", "%25");
        replacements.put("+", "%2B");
        replacements.put(" ", "%20");
        replacements.put("#", "%23");
        replacements.put("\"", "%22");
        replacements.put("<", "%3C");
        replacements.put(">", "%3E");
        replacements.put("{", "%7B");
        replacements.put("}", "%7D");
        replacements.put("|", "%7C");
        replacements.put("\\", "%5C");
        replacements.put("^", "%5E");
        replacements.put("~", "%7E");
        replacements.put("[", "%5B");
        replacements.put("]", "%5D");
        replacements.put("`", "%60");
        replacements.put(";", "%3B");
        replacements.put("/", "%2F");
        replacements.put("?", "%3F");
        replacements.put(":", "%3A");
        replacements.put("@", "%40");
        replacements.put("=", "%3D");
        replacements.put("&", "%26");
        replacements.put("$", "%24");
        replacements.put("-", "%2D");
        replacements.put("_", "%5F");
        replacements.put(".", "%2E");
        replacements.put("!", "%21");
        replacements.put("*", "%2A");
        replacements.put("'", "%27");
        replacements.put("(", "%28");
        replacements.put(")", "%29");
        replacements.put(",", "%2C");
        
        for (String s : replacements.keySet()) {
            query = query.replaceAll(Pattern.quote(s), replacements.get(s));
        }
        
        return query;
    }
}