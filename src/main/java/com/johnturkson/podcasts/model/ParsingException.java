package com.johnturkson.podcasts.model;

public class ParsingException extends Exception {
    public ParsingException() {
        super();
    }
    
    public ParsingException(String message) {
        super(message);
    }
}
