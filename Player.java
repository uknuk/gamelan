package gamelan;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Orientation;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class Player {

  private Lib lib = new Lib();
  private Map<String, FlowPane> panes = new HashMap<>();
  private Map<String, Label> labels = new HashMap<>();
  private Button btn = new Button();
  private String art;
  private String selected;
  private String alb;
  private int    pos;

  public Player(TabPane root) {
    String[] pNames = {"tracks", "alb", "art"};
    Arrays.asList(pNames)
      .forEach(name -> panes.put(name, new FlowPane(Orientation.HORIZONTAL)));

    HBox control = new HBox(10);
    ObservableList<Node> children = control.getChildren();
    String[] lNames = {"art", "alb", "track", "sel"};
    Arrays.asList(lNames)
      .forEach(name -> {
          Label lbl = new Label();
          children.add(lbl);
          lbl.setStyle("-fx-text-fill: green");
          labels.put(name, lbl);
        });

    btn.setStyle("-fx-text-fill: red");
    children.add(btn);

    Label lbl = new Label("Tracks");
    lbl.setStyle("-fx-text-fill: green");

    VBox grid = new VBox(10);
    grid.getChildren()
      .addAll(control, lbl, lib.makeScroll(panes.get("tracks")), 
              labels.get("sel"), panes.get("alb"));

    ObservableList<Tab> tabs = root.getTabs();
    tabs.add(new Tab("Player", grid));
    tabs.add(new Tab("Select", lib.makeScroll(panes.get("art"))));
    root.getSelectionModel().select(0);
    loadLast();
    loadArtists();
  }



  private void loadLast() {
    try {
      String[] lines = Files.lines(Paths.get(System.getenv("HOME") + "/.local/mlast"))
        .toArray(String[]::new);
      String artist = lines[0];
      pos = Integer.parseInt(lines[2]);
      System.out.println(pos);
    }
    catch (IOException ex) {
      System.out.println("File ~/.local/mlast not found");
    }
  }

  private void loadArtists() {
    try {
      String dir = 
              Files.lines(Paths.get(System.getenv("HOME") + "/.config/mdirs"))
              .toArray(String[]::new)[0];

      // first version assumes only one dir
      ArrayList<String> arts = 
              new ArrayList<>(Arrays.asList((new File(dir)).list()));
      show(arts, "art", ad -> loadAlbums(dir + '/' + ad));
    }
    catch (IOException ex) {
      System.out.println("File ~/.config/mdirs not found");
    }
  }
    
  private void loadAlbums(String dir) {
    System.out.println(dir);
  }

  private void show(ArrayList<String> files, String kind, 
          Consumer<String> fun) {
    ObservableList<Node> ch = panes.get(kind).getChildren();
    ch.clear();
    files.forEach(file -> 
            ch.add(lib.makeButton(file, kind, e -> fun.accept(file))));
  }

}
