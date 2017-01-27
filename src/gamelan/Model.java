package gamelan;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Model {

  final String LAST_FILE = "/.local/mlast";
  final String MUS_DIRS = "/.config/mdirs";
  final String HOME = System.getenv("HOME");

  Controller control;
  Map<String, String> arts = new TreeMap<>();
  String art;
  String selArt;
  String alb;
  ArrayList<String> tracks;
  ArrayList<String> albs;
  ArrayList<String> selAlbs;
  MediaPlayer mp;
  int  nTrack;
  int nAlb;

  Model(Controller control) {
    this.control = control;
      loadLast();
      loadArtists();
  }


  void loadLast() {
    try {
      String[] lines = Files.lines(Paths.get(HOME + LAST_FILE))
          .toArray(String[]::new);
      selArt = lines[0];
      nTrack = Integer.parseInt(lines[2]);
      System.out.println(nTrack);
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", LAST_FILE);
    }
  }

  SortedSet<String> loadArtists() {
    try {
      String[] dirs = Files
          .lines(Paths.get(HOME + MUS_DIRS))
          .toArray(String[]::new)[0]
          .replace("\\n+", "")
          .split("\\s+");

      Arrays.asList(dirs).forEach(dir ->
          Lib.load(dir).forEach(art ->
              arts.put(art, Lib.join(dir, art))));

      return new TreeSet(arts.keySet());
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", MUS_DIRS);
      return null;
    }
  }

  ArrayList<String> selectArtist(String art) {
    selArt = art;
    selAlbs = Lib.load(arts.get(art));
    return selAlbs;
  }

  ArrayList<String> selectAlbum(String alb) {
    return selectAlbum(alb, 0);
  }


  ArrayList<String> selectAlbum(String alb, int nTrack) {
    art = selArt;
    this.alb = alb;
    albs = selAlbs;
    nAlb = albs.indexOf(alb);
    loadTracks(Lib.join(arts.get(art), alb));
    this.nTrack = nTrack;
    playTrack(tracks.get(nTrack));
    return tracks;
  }

  void playTrack(String track) {
    if (mp != null)
      mp.stop();

    mp = new MediaPlayer(new Media(new File(track).toURI().toString()));
    mp.play();
    mp.setOnEndOfMedia(() -> play(-1));
  }

  ArrayList<String> loadTracks(String dir) {
    List<String> files = Lib.tracks(dir);
    tracks = Lib.addPath(files, dir);
    return tracks;
  }



  void play(int n) {
    if (n == -1)
      nTrack++;
    else
      nTrack = n;

    playTrack(tracks.get(nTrack));
  }








}
