package gamelan;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;

import java.util.*;
import java.util.function.Consumer;


public class Controller {
  Model model;
  Lib  lib;

  @FXML TabPane root;

  @FXML Label artist;
  @FXML Label album;

  @FXML Button pause;

  @FXML FlowPane tracks;
  @FXML FlowPane artists;
  @FXML FlowPane albums;

  @FXML public void initialize() {
    Lib.init();
    model = new Model(this);

    showArtists(model.loadArtists());
      root.getSelectionModel().select(0);
  }

  void showArtists(Set<String> arts) {
    ObservableList<Node> ch = artists.getChildren();

    arts.forEach(art ->
        ch.add(lib.makeButton(art, "arts", e -> selectArtist(art))));

  }

  void selectArtist(String art) {
    show(model.selectArtist(art), albums, "albs", a -> selectAlbum(a));
  }

  void selectAlbum(String alb) {
    show(model.selectAlbum(alb), tracks, "tracks", t -> selectTrack(t));
  }

  void selectTrack(String track) {
    model.playTrack(track);
  }

  private void show(ArrayList<String> files, FlowPane pane, String kind,
                    Consumer<String> fun) {

    ObservableList<Node> ch = pane.getChildren();
    ch.clear();

    files.forEach(file ->
      ch.add(lib.makeButton(file, kind, e -> fun.accept(file))));
  }

}

