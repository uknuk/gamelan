package gamelan;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


class Model {
  private final String LAST_FILE = ".local/mlast";
  private final String DIRS_FILE = ".config/mdirs";

  private final String home;
  private String lastFile;
  private final Controller control;
  private final Map<String, String> arts = new TreeMap<>();
  private String art;
  private String selArt;
  private String alb;
  private ArrayList<String> tracks;
  private ArrayList<String> albs;
  private ArrayList<String> selAlbs;
  private MediaPlayer mp;
  private int  nTrack;
  private int nAlb;

  Model(Controller control) {
    this.control = control;
    home = System.getProperty("os.name").equals("Linux")
            ? System.getenv("HOME")
            : System.getenv("USERPROFILE");

    lastFile = FilenameUtils.concat(home, LAST_FILE);
    //dirsFile = FilenameUtils.concat(home, dirsFile);
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

  ArrayList<String> loadArtists() {
    try {
      String[] dirs = Files
          .lines(Paths.get(FilenameUtils.concat(home, DIRS_FILE)))
          .toArray(String[]::new)[0]
          .replace("\\n+", "")
          .split("\\s+");

      Arrays.asList(dirs).forEach(dir ->
          Lib.load(dir).forEach(art ->
              arts.put(art, Lib.join(dir, art))));

      arts.remove("desktop.ini");

      return new ArrayList(arts.keySet());
    }
    catch (IOException ex) {
      System.out.printf("File %s not found", DIRS_FILE);
      return null;
    }
  }

  ArrayList<String> selectArtist(String art) {
    selArt = art;
    selAlbs = Lib.load(arts.get(art));
    return selAlbs;
  }

  ArrayList<String> selectAlbum(String alb, int nTrack) {
    if (!Objects.equals(art, selArt)) {
      art = selArt;
      albs = selAlbs;
    }
    else
      control.restore("albs", nAlb);

    this.alb = alb;
    nAlb = albs.indexOf(alb);
    control.activate("albs", nAlb);
    control.artist.setText(" " + art);
    control.album.setText(" " + alb);
    tracks = Lib.loadTracks(arts.get(art), alb);
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

  private void play(int n) {
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

  private void save() {
    try {
      OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(lastFile),"UTF-8");
      String[] data = {art, alb, Integer.toString(nTrack)};
      Arrays.asList(data).forEach(val -> {
            try {
              fw.write(val + System.getProperty("line.separator"));
            } catch (IOException ex) {
              System.out.print("Could not write to last file");
            }
          });
      fw.close();
    }
    catch (IOException ex) {
      System.out.print("Could not open last file");
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

  private String format(double time) {
    return LocalTime.ofSecondOfDay((long) time)
        .format(DateTimeFormatter.ofPattern("mm:ss"));
  }

}
