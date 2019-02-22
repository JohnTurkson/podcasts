package com.johnturkson.podcasts.tools;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {
    private static Status status = Status.IDLE;
    private static List<Download> queue = new ArrayList<>();
    
    public static void download(URL source, Path location) {
        // TODO better solution (more helpers?)
        if (!queue.contains(new Download(source, location))) {
            queue.add(new Download(source, location));
        }
        
        if (status == Status.IDLE) {
            ExecutorService downloader = Executors.newCachedThreadPool();
            downloader.submit(() -> {
                status = Status.DOWNLOADING;
                while (!queue.isEmpty()) {
                    try {
                        if (Files.notExists(queue.get(0).getLocation())) {
                            Files.createDirectories(queue.get(0).getLocation().getParent());
                            Files.createFile(queue.get(0).getLocation());
                        }
                        Files.copy(queue.get(0).getSource().openStream(),
                                queue.get(0).getLocation(),
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
    
    public static void cancel(URL source, Path location) {
        queue.remove(new Download(source, location));
    }
    
    public enum Status {
        IDLE,
        DOWNLOADING
    }
    
    public static class Download {
        private URL source;
        private Path location;
        
        public Download(URL source, Path location) {
            this.source = source;
            this.location = location;
        }
        
        public URL getSource() {
            return source;
        }
        
        public Path getLocation() {
            return location;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Download download = (Download) o;
            return source.equals(download.source) &&
                    location.equals(download.location);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(source, location);
        }
    }
}
