package com.johnturkson.podcasts.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class URLReader {
    private URL url;
    
    public URLReader(URL url) {
        this.url = url;
    }
    
    public String read() {
        StringBuilder contents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line).append("\n");
            }
        } catch (IOException x) {
            // ignored - if the source is not able to be read, or if no response is obtained, 
            // null is returned
            return null;
        }
        return contents.toString();
    }
}