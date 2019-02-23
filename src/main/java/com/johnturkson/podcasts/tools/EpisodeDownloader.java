package com.johnturkson.podcasts.tools;

import com.johnturkson.podcasts.model.Episode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpisodeDownloader {
    // TODO download progress
    // TODO some kind of listener?
    // TODO notify when complete
    // TODO speed (mbps)
    private static Status status = Status.IDLE;
    private static List<Episode> queue = new ArrayList<>();
    private static ExecutorService downloader = Executors.newSingleThreadExecutor();
    
    public static void download(Episode episode) {
        if (!queue.contains(episode)) {
            queue.add(episode);
        }
        
        if (status == Status.IDLE) {
            downloader.submit(() -> {
                status = Status.DOWNLOADING;
                while (!queue.isEmpty()) {
                    try {
                        if (Files.notExists(queue.get(0).getEpisodeLocation())) {
                            Files.createDirectories(queue.get(0).getEpisodeLocation().getParent());
                            Files.createFile(queue.get(0).getEpisodeLocation());
                        }
                        Files.copy(queue.get(0).getSource().openStream(),
                                queue.get(0).getEpisodeLocation(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException x) {
                        x.printStackTrace();
                        // TODO
                    }
                    queue.remove(0);
                }
                status = Status.IDLE;
            });
            downloader.shutdown();
        }
    }
    
    public enum Status {
        IDLE,
        DOWNLOADING
    }
}
