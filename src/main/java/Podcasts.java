import com.johnturkson.podcasts.model.Library;
import com.johnturkson.podcasts.model.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Podcasts extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO match web search to local podcast metadata tags (subscribed, favorite etc)
        // when searching itunes the metadata from the feed doesn't have this information, which
        // defautls to "false"
        // fix: in the PodcastSearcher check if a local copy exists, if it does, update the non 
        // local metadata
        Parent root = FXMLLoader.load(getClass().getResource("UI.fxml"));
        primaryStage.setTitle("Podcasts");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        Player.getInstance().export();
    }
    
    public static void main(String[] args) {
        Library.getInstance().restore();
        Player.getInstance().restore();
        launch(args);
    }
}
