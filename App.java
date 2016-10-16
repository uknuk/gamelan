/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelan;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author bma
 */
public class App extends Application {
    
    private TabPane root = new TabPane();
    private Player  player = new Player(root);
    
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
