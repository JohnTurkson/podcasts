package com.johnturkson.podcasts;

import com.johnturkson.podcasts.model.Podcast;
import com.johnturkson.podcasts.search.PodcastSearcher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        PodcastSearcher podcastSearcher = new PodcastSearcher();
        podcastSearcher.setLimit(1);
        Podcast p;
        p = podcastSearcher.search("freakanomics").get(0);
        try {
            p.export();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p = podcastSearcher.search("planet money").get(0);
        try {
            p.export();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p = podcastSearcher.search("reply all").get(0);
        try {
            p.export();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    /*
    List of working podcasts:
    All NPR Podcasts
    All Gimlet Podcasts
    Freakanomics Radio
     */
    
    /*
    List of broken podcasts:
    
     */
}
