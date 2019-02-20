package com.johnturkson.podcasts.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Library {
    public static final Path DEFAULT_LOCATION = Paths.get(System.getProperty("user.home"))
            .resolve("Documents").resolve("Podcasts");
    
    private List<Podcast> subscribedPodcasts;
    private List<Podcast> favoritePodcasts;
    private List<Episode> savedEpisodes;
    private List<Episode> favoriteEpisodes;
    
    public Library() {
        this.subscribedPodcasts = new ArrayList<>();
        this.favoritePodcasts = new ArrayList<>();
        this.savedEpisodes = new ArrayList<>();
        this.favoriteEpisodes = new ArrayList<>();
    }
    
    public List<Podcast> getSubscribedPodcasts() {
        return subscribedPodcasts;
    }
    
    public List<Podcast> getFavoritePodcasts() {
        return favoritePodcasts;
    }
    
    public List<Episode> getSavedEpisodes() {
        return savedEpisodes;
    }
    
    public List<Episode> getFavoriteEpisodes() {
        return favoriteEpisodes;
    }
}
