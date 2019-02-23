package com.johnturkson.podcasts.tools;

import com.johnturkson.podcasts.model.Episode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public class EpisodeDeleter {
    public static void delete(Episode episode) {
        try {
            Files.deleteIfExists(episode.getEpisodeLocation());
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
}
