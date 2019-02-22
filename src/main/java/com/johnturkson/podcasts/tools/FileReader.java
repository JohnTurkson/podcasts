package com.johnturkson.podcasts.tools;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {
    private Path path;
    
    public FileReader(Path path) {
        this.path = path;
    }
    
    public String read() {
        try {
            return Files.readString(path);
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
}
