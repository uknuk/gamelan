package gamelan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gamelan.fxml"));
        primaryStage.setTitle("Gamelan");
        primaryStage.setScene(new Scene(root, Lib.WIDTH, Lib.WIDTH/1.8));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
