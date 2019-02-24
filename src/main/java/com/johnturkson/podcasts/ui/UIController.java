package com.johnturkson.podcasts.ui;

import com.johnturkson.podcasts.model.Episode;
import com.johnturkson.podcasts.model.Library;
import com.johnturkson.podcasts.model.Player;
import com.johnturkson.podcasts.model.Podcast;
import com.johnturkson.podcasts.search.PodcastSearcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    
    @FXML
    private VBox playerContainer;
    @FXML
    private Button playButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label podcastLabel;
    @FXML
    private Label episodeLabel;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeLabel;
    
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
        
        Button playButton = new Button("Play");
        playButton.setOnAction(actionEvent -> playEpisode(episode));
        episodeDisplay.getChildren().add(playButton);
        
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
        
        libraryDisplay.getChildren().add(new Label("Favorites"));
        for (Podcast podcast : Library.getInstance().getFavoritePodcasts()) {
            Button favorite = new Button(podcast.getTitle());
            favorite.setOnAction(actionEvent -> displayPodcast(podcast));
            libraryDisplay.getChildren().add(favorite);
        }
        
        libraryDisplay.getChildren().add(new Label("Episodes"));
        libraryDisplay.getChildren().add(new Label("Downloaded"));
        for (Episode episode : Library.getInstance().getDownloadedEpisodes()) {
            Button downloaded = new Button(episode.getTitle());
            downloaded.setOnAction(actionEvent -> displayEpisode(episode));
            libraryDisplay.getChildren().add(downloaded);
        }
        
        libraryDisplay.getChildren().add(new Label("Favorites"));
        for (Episode episode : Library.getInstance().getFavoriteEpisodes()) {
            Button favorite = new Button(episode.getTitle());
            favorite.setOnAction(actionEvent -> displayEpisode(episode));
            libraryDisplay.getChildren().add(favorite);
        }
        
        centerPane.getChildren().forEach(child -> child.setVisible(false));
        libraryDisplayPane.setVisible(true);
        libraryDisplayPane.toFront();
    }
    
    public void playEpisode(Episode episode) {
        playButton.setDisable(false);
        previousButton.setDisable(false);
        nextButton.setDisable(false);
        timeSlider.setDisable(false);
        timeLabel.setDisable(false);
        
        Player.getInstance().play(episode);
        podcastLabel.setText(episode.getPodcast().getTitle());
        episodeLabel.setText(episode.getTitle());
        
        Player.getInstance().getMediaPlayer().setOnPlaying(() -> {
            playButton.setText("Pause");
            playButton.setOnAction(actionEvent -> Player.getInstance().pause());
        });
        
        Player.getInstance().getMediaPlayer().setOnPaused(() -> {
            playButton.setText("Play");
            playButton.setOnAction(actionEvent -> Player.getInstance().play());
        });
        
        Player.getInstance().getMediaPlayer().setOnStopped(() -> {
            playButton.setText("Play");
            playButton.setOnAction(actionEvent -> Player.getInstance().play());
        });
        
        Player.getInstance().getMediaPlayer().currentTimeProperty().addListener(observable -> {
            timeSlider.setValue(Player.getInstance().getTotalTime() == 0 ? 0 :
                    (double) Player.getInstance().getCurrentTime() / (double) Player.getInstance().getTotalTime() * 100);
            timeLabel.setText(Player.getInstance().getCurrentTime() + " / " + Player.getInstance().getTotalTime());
        });
        
        timeSlider.valueProperty().addListener(observable -> {
            if (timeSlider.isValueChanging()) {
                timeLabel.setText((int) (Player.getInstance().getTotalTime() * timeSlider.getValue() / 100) +
                        "/" + Player.getInstance().getTotalTime());
                Player.getInstance().seek((int) (Player.getInstance().getTotalTime() * timeSlider.getValue() / 100));
            }
        });
    }
}
