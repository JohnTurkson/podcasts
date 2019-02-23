package com.johnturkson.podcasts.ui;

import com.johnturkson.podcasts.model.Episode;
import com.johnturkson.podcasts.model.Library;
import com.johnturkson.podcasts.model.Podcast;
import com.johnturkson.podcasts.search.PodcastSearcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class UIController {
    @FXML
    private TextField searchBar;
    @FXML
    private StackPane centerPane;
    @FXML
    private VBox searchDisplay;
    @FXML
    private ScrollPane searchDisplayPane;
    @FXML
    private VBox podcastDisplay;
    @FXML
    private ScrollPane podcastDisplayPane;
    @FXML
    private VBox episodeDisplay;
    @FXML
    private ScrollPane episodeDisplayPane;
    @FXML
    private VBox libraryDisplay;
    @FXML
    private ScrollPane libraryDisplayPane;
    
    public void search() {
        searchDisplay.getChildren().clear();
    
        searchDisplay.getChildren().add(new Label("Results"));
        
        for (Podcast result : new PodcastSearcher().search(searchBar.getText())) {
            Button item = new Button(result.getTitle());
            item.setOnAction(actionEvent -> displayPodcast(result));
            searchDisplay.getChildren().add(item);
        }
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        searchDisplayPane.setVisible(true);
        searchDisplayPane.toFront();
    }
    
    public void displayPodcast(Podcast podcast) {
        // TODO fix description text not wrapping
        podcastDisplay.getChildren().clear();
        
        Label title = new Label(podcast.getTitle());
        Label author = new Label(podcast.getAuthor());
        Label description = new Label(podcast.getDescription());
        
        title.setWrapText(true);
        author.setWrapText(true);
        description.setWrapText(true);
        
        podcastDisplay.getChildren().add(title);
        podcastDisplay.getChildren().add(author);
        podcastDisplay.getChildren().add(description);
    
        Button subscribeButton = new Button(podcast.isSubscribed() ? "Unsubscribe" : "Subscribe");
        subscribeButton.setOnAction(actionEvent -> {
            if (podcast.isSubscribed()) {
                podcast.unsubscribe();
                subscribeButton.setText("Subscribe");
            } else {
                podcast.subscribe();
                subscribeButton.setText("Unsubscribe");
            }
        });
        podcastDisplay.getChildren().add(subscribeButton);
        
        podcastDisplay.getChildren().add(new Label("Episodes"));
        for (Episode episode : podcast.getEpisodes()) {
            Button item = new Button(episode.getTitle());
            item.setOnAction(actionEvent -> displayEpisode(episode));
            podcastDisplay.getChildren().add(item);
        }
        
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        podcastDisplayPane.setVisible(true);
        podcastDisplayPane.toFront();
    }
    
    public void displayEpisode(Episode episode) {
        episodeDisplay.getChildren().clear();
        
        Button podcast = new Button(episode.getPodcast().getTitle());
        podcast.setOnAction(actionEvent -> displayPodcast(episode.getPodcast()));
        
        Label title = new Label(episode.getTitle());
        Label description = new Label(episode.getDescription());
        
        title.setWrapText(true);
        description.setWrapText(true);
        
        episodeDisplay.getChildren().add(podcast);
        episodeDisplay.getChildren().add(title);
        episodeDisplay.getChildren().add(description);
        
        Button downloadButton = new Button(episode.isDownloaded() ? "Delete" : "Download");
        downloadButton.setOnAction(actionEvent -> {
            if (episode.isDownloaded()) {
                episode.delete();
                downloadButton.setText("Download");
            } else {
                episode.download();
                downloadButton.setText("Delete");
            }
        });
        episodeDisplay.getChildren().add(downloadButton);
        
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        episodeDisplayPane.setVisible(true);
        episodeDisplayPane.toFront();
    }
    
    public void displaySearch() {
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        searchDisplayPane.setVisible(true);
        searchDisplayPane.toFront();
    }
    
    public void displayLibrary() {
        libraryDisplay.getChildren().clear();
        
        libraryDisplay.getChildren().add(new Label("Podcasts"));
        libraryDisplay.getChildren().add(new Label("Subscribed"));
        for (Podcast podcast : Library.getInstance().getSubscribedPodcasts()) {
            Button subscribed = new Button(podcast.getTitle());
            subscribed.setOnAction(actionEvent -> displayPodcast(podcast));
            libraryDisplay.getChildren().add(subscribed);
        }
        
//        libraryDisplay.getChildren().add(new Label("Favorites"));
//        for (Podcast podcast : Library.getInstance().getFavoritePodcasts()) {
//            Button favorite = new Button(podcast.getTitle());
//            favorite.setOnAction(actionEvent -> displayPodcast(podcast));
//            libraryDisplay.getChildren().add(favorite);
//        }
        
        libraryDisplay.getChildren().add(new Label("Episodes"));
        libraryDisplay.getChildren().add(new Label("Downloaded"));
        for (Episode episode : Library.getInstance().getDownloadedEpisodes()) {
            Button downloaded = new Button(episode.getTitle());
            downloaded.setOnAction(actionEvent -> displayEpisode(episode));
            libraryDisplay.getChildren().add(downloaded);
        }
        
//        libraryDisplay.getChildren().add(new Label("Favorites"));
//        for (Episode episode : Library.getInstance().getFavoriteEpisodes()) {
//            Button favorite = new Button(episode.getTitle());
//            favorite.setOnAction(actionEvent -> displayEpisode(episode));
//            libraryDisplay.getChildren().add(favorite);
//        }
        
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        libraryDisplayPane.setVisible(true);
        libraryDisplayPane.toFront();
    }
}
