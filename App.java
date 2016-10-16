
package gamelan;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class App extends Application {
    
    private final TabPane root = new TabPane();
    private final Player  player = new Player(root);
    
    @Override
    public void start(Stage stage) {
    root.setOnKeyReleased(e -> handle(e));
    Scene scene = new Scene(root, 1024, 572);
    stage.setScene(scene);
    stage.setTitle("Gamelan");
    stage.show();

    }
    
    public void handle(KeyEvent e) {
    switch(e.getCode()) {
    case LESS:
    case BACK_QUOTE:
      SingleSelectionModel<Tab> sm = root.getSelectionModel();
      sm.select(1 - sm.getSelectedIndex());
      break;
    }
  }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}
