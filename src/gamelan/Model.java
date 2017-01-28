package gamelan;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Model {
  final String HOME = System.getenv("HOME");
  final String LAST_FILE = HOME + "/.local/mlast";
  final String MUS_DIRS = HOME + "/.config/mdirs";


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
    System.out.println(System.getProperty("os.name"));
  }


  void loadLast() {
    try {
      String[] lines = Files.lines(Paths.get(LAST_FILE))
          .toArray(String[]::new);
      control.selectArtist(lines[0]);
      control.selectAlbum(lines[1], Integer.parseInt(lines[2]));
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", LAST_FILE);
    }
  }

  SortedSet<String> loadArtists() {
    try {
      String[] dirs = Files
          .lines(Paths.get(MUS_DIRS))
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

  ArrayList<String> selectAlbum(String alb, int nTrack) {
    art = selArt;
    this.alb = alb;
    albs = selAlbs;
    nAlb = albs.indexOf(alb);
    control.artist.setText(art);
    control.album.setText(alb);
    tracks = Lib.tracks(arts.get(art), alb);
    this.nTrack = nTrack;
    playTrack(tracks.get(nTrack));
    return tracks;
  }

  void playTrack(String track) {
    if (mp != null)
      mp.stop();

    control.track.setText(track);
    String file = Lib.join(arts.get(art), alb, track);
    Media m = new Media(new File(file).toURI().toString());
    mp = new MediaPlayer(m);
    mp.setOnReady(() ->  System.out.println(m.getDuration()));
    mp.play();
    save();
    control.btn.setText("||");
    mp.setOnEndOfMedia(() -> play(-1));
  }

  void play(int n) {
    if (n == -1)
      nTrack++;
    else
      nTrack = n;

    playTrack(tracks.get(nTrack));
  }

  void save() {
    try {
      FileWriter fw = new FileWriter(LAST_FILE);
      String[] data = {art, alb, Integer.toString(nTrack)};
      Arrays.asList(data).forEach(val -> {
            try {
              fw.write(val + System.getProperty("line.separator"));
            } catch (IOException ex) {
              System.out.printf("Could not write to last file");
            }
          });
      fw.close();
    }
    catch (IOException ex) {
      System.out.printf("Could not open last file");
    }
  }

  void changePlay() {
    Status status = mp.getStatus();
    if (status == Status.PLAYING) {
      mp.pause();
      control.btn.setText(">");
    } else if (status == Status.PAUSED) {
      mp.play();
      control.btn.setText("||");
    }
  }

}
