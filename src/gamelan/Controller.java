package gamelan;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;


public class Controller {
  Model model;
  Lib  lib;

  @FXML TabPane root;

  @FXML Label artist;
  @FXML Label album;
  @FXML Label track;

  @FXML Button btn;

  @FXML FlowPane tracks;
  @FXML FlowPane artists;
  @FXML FlowPane albums;
  @FXML FlowPane info;

  @FXML public void initialize() {
    Lib.init();

    FlowPane[] panes = {artists, albums, tracks};
    Arrays.asList(panes).forEach(p -> p.setPrefWidth(Lib.WIDTH));
    info.setPrefWidth(Lib.WIDTH*0.9);

    model = new Model(this);
    showArtists(model.loadArtists());
    model.loadLast();
    //root.getSelectionModel().select(1);

  }

  void showArtists(Set<String> arts) {
    ObservableList<Node> ch = artists.getChildren();

    arts.forEach(art ->
        ch.add(lib.makeButton(art, "arts", e -> selectArtist(art))));

  }

  void selectArtist(String art) {
    root.getSelectionModel().select(0);
    show(model.selectArtist(art), albums, "albs", a -> selectAlbum(a));
  }

  void selectAlbum(String alb) {
    selectAlbum(alb, 0);
  }

  void selectAlbum(String alb, int nTrack) {
    show(model.selectAlbum(alb, nTrack), tracks, "tracks", t -> selectTrack(t));
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

  @FXML
  void pauseHandler(ActionEvent ev) {
    model.changePlay();
  }

}

