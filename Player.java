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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Player {

  private final Lib lib = new Lib();
  private Map<String, FlowPane> panes = new HashMap<>();
  private Map<String, Label> labels = new HashMap<>();
  private final Button btn = new Button();
  private final TabPane root;
  private String art;
  private String selArt;
  private String alb;
  private List<String> tracks;
  private List<String> albs;
  private List<String> selAlbs;
  private MediaPlayer mp;
  private int    pos;

  public Player(TabPane rootPane) {
    root = rootPane;
    String[] pNames = {"tracks", "albs", "selAlbs", "arts"};
    Arrays.asList(pNames)
      .forEach(name -> panes.put(name, new FlowPane(Orientation.HORIZONTAL)));

    HBox control = new HBox(10);
    ObservableList<Node> children = control.getChildren();
    String[] lNames = {"art", "alb", "track", "selArt"};
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

    VBox pGrid = new VBox(10);
    pGrid.getChildren()
      .addAll(control, lbl, lib.makeScroll(panes.get("tracks")), 
              panes.get("albs"));
    
    VBox sGrid = new VBox(5);
    sGrid.getChildren()
      .addAll(labels.get("selArt"), panes.get("selAlbs"),
              lib.makeScroll(panes.get("arts")));

    ObservableList<Tab> tabs = root.getTabs();
    tabs.add(new Tab("Player", pGrid));
    tabs.add(new Tab("Select", sGrid));
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
      show(lib.load(dir), "arts", ard -> loadAlbums(lib.join(dir, ard)));
    }
    catch (IOException ex) {
      System.out.println("File ~/.config/mdirs not found");
    }
  }
    
  private void loadAlbums(String dir) {    
    selAlbs = lib.load(dir);
    selArt = dir;
    labels.get("selArt").setText(lib.base(dir) + ':');
    show(selAlbs, "selAlbs", ald -> selectAlbum(lib.join(dir, ald)));
  }
  
  private void selectAlbum(String selAlb) {
    root.getSelectionModel().select(0);
    art = selArt;
    alb = selAlb;
    albs = lib.addPath(selAlbs, selArt);
    show(albs, "albs", ald -> selectAlbum(ald));
    loadTracks(alb);
  }
  
  private void loadTracks(String dir) {
    List<String> files = lib.tracks(dir);
    tracks = lib.addPath(files, dir);
    panes.get("tracks").getChildren().clear();
    show(files, "tracks", track -> play(tracks.indexOf(lib.join(dir, track))));
    play(0);
  }
  
  
  private void play(int n) {
    if (n == -1) 
      pos++;
    else
      pos = n;
    
    playTrack(tracks.get(pos));
  }
  
  private void playTrack(String track) {
    if (mp != null)
      mp.stop();
    
    mp = new MediaPlayer(new Media(new File(track).toURI().toString()));
    mp.play();
    mp.setOnEndOfMedia(() -> play(-1));
  }  

  private void show(List<String> files, String kind, 
          Consumer<String> fun) {
    ObservableList<Node> ch = panes.get(kind).getChildren();
    ch.clear();
    
    files.forEach(file -> 
            ch.add(lib.makeButton(file, kind, e -> fun.accept(file))));
  }

}