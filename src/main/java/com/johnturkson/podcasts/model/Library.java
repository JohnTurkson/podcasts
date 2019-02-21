package com.johnturkson.podcasts.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Library {
    public static final Path DEFAULT_LOCATION = Paths.get(System.getProperty("user.home"))
            .resolve("Documents").resolve("Podcasts");
    private static final Library INSTANCE = new Library();
    
    private Set<Podcast> subscribedPodcasts;
    private Set<Podcast> favoritePodcasts;
    private Set<Episode> downloadedEpisodes;
    private Set<Episode> favoriteEpisodes;
    
    private Library() {
        this.subscribedPodcasts = new HashSet<>();
        this.favoritePodcasts = new HashSet<>();
        this.downloadedEpisodes = new HashSet<>();
        this.favoriteEpisodes = new HashSet<>();
    }
    
    public static Library getInstance() {
        return INSTANCE;
    }
    
    public void restore() {
        try {
            Files.list(DEFAULT_LOCATION)
                    .filter(d -> Files.isDirectory(d))
                    .filter(d -> Files.exists(d.resolve("metadata.xml")))
                    .forEach(d -> {
                        try {
                            Podcast p = Podcast.parse(d.resolve("metadata.xml"));
                            if (p.isSubscribed()) {
                                subscribedPodcasts.add(p);
                            }
                            
                            if (p.isFavorite()) {
                                favoritePodcasts.add(p);
                            }
                            
                            p.getEpisodes().forEach(e -> {
                                if (e.isDownloaded()) {
                                    downloadedEpisodes.add(e);
                                }
                                if (e.isFavorite()) {
                                    favoriteEpisodes.add(e);
                                }
                            });
                            
                        } catch (ParsingException x) {
                            // ignored - if podcast cannot be parsed
                        }
                    });
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
    
    
    public Set<Podcast> getSubscribedPodcasts() {
        return subscribedPodcasts;
    }
    
    public Set<Podcast> getFavoritePodcasts() {
        return favoritePodcasts;
    }
    
    public Set<Episode> getDownloadedEpisodes() {
        return downloadedEpisodes;
    }
    
    public Set<Episode> getFavoriteEpisodes() {
        return favoriteEpisodes;
    }
}
