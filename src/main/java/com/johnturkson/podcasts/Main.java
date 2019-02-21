package com.johnturkson.podcasts;

import com.johnturkson.podcasts.model.Podcast;
import com.johnturkson.podcasts.search.PodcastSearcher;

public class Main {
    public static void main(String[] args) {
        Podcast p = new PodcastSearcher().search("Planet Money").get(0);
        p.export();
        System.out.println(p);
    }
}
