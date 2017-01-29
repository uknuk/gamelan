package gamelan;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Model {
  String home;
  String lastFile = ".local/mlast";
  String dirsFile = ".config/mdirs";


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
    home = System.getProperty("os.name") == "Linux"
            ? System.getenv("HOME")
            : System.getenv("USERPROFILE");

    lastFile = FilenameUtils.concat(home, lastFile);
    dirsFile = FilenameUtils.concat(home, dirsFile);
  }


  void loadLast() {
    try {
      String[] lines = Files.lines(Paths.get(lastFile))
          .toArray(String[]::new);
      control.selectArtist(lines[0]);
      control.selectAlbum(lines[1], Integer.parseInt(lines[2]));
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", lastFile);
    }
  }

  SortedSet<String> loadArtists() {
    try {
      String[] dirs = Files
          .lines(Paths.get(dirsFile))
          .toArray(String[]::new)[0]
          .replace("\\n+", "")
          .split("\\s+");

      Arrays.asList(dirs).forEach(dir ->
          Lib.load(dir).forEach(art ->
              arts.put(art, Lib.join(dir, art))));

      return new TreeSet(arts.keySet());
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", dirsFile);
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
    control.restore("albs", nAlb);
    nAlb = albs.indexOf(alb);
    control.activate("albs", nAlb);
    control.artist.setText(" " + art);
    control.album.setText(" " + alb);
    tracks = Lib.tracks(arts.get(art), alb);
    this.nTrack = nTrack;
    playTrack(tracks.get(nTrack));
    return tracks;
  }

  void playTrack(String track) {
    if (mp != null)
      mp.stop();

    control.track.setText(" " + track);
    String file = Lib.join(arts.get(art), alb, track);
    mp = new MediaPlayer(new Media(new File(file).toURI().toString()));
    mp.play();
    save();
    control.btn.setText("||");
    mp.setOnEndOfMedia(() -> play(-1));
  }

  void play(int n) {
    control.restore("tracks", nTrack);

    if (n == -1) {
      int next;
      next = nTrack + 1;
      if (next < tracks.size())
        nTrack = next;
      else {
        next = nAlb + 1;
        if (next < albs.size()) {
          control.selectAlbum(albs.get(next));
        }
        return;
      }
    }
    else
      nTrack = n;

    control.activate("tracks", nTrack);
    playTrack(tracks.get(nTrack));
  }

  void save() {
    try {
      FileWriter fw = new FileWriter(lastFile);
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

  void getDuration() {
    if (mp != null && mp.getStatus() == Status.PLAYING) {
      double duration = mp.getTotalDuration().toSeconds();
      double current = mp.getCurrentTime().toSeconds();
      control.slider.setValue(current/duration);
      control.current.setText("  " + format(current));
      control.duration.setText(format(duration));
    }
  }

  String format(double time) {
    return LocalTime.ofSecondOfDay((long) time)
        .format(DateTimeFormatter.ofPattern("mm:ss"));
  }

}
