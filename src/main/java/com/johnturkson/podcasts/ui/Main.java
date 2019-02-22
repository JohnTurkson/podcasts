package com.johnturkson.podcasts.ui;

import com.johnturkson.podcasts.model.Podcast;
import com.johnturkson.podcasts.search.PodcastSearcher;

public class Main {
    public static void main(String[] args) {
        Podcast p = new PodcastSearcher().search("Planet Money").get(0);
        p.export();
        System.out.println(p);
    
        p.getEpisodes().get(0).download();
        p.getEpisodes().get(1).download();
        p.getEpisodes().get(2).download();
    }
}
