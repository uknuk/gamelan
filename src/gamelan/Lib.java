package gamelan;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Lib {

  private static final int[] TXT_SIZES = {25, 40, 30};
  //private static final int[] FONT_SIZES = {14, 14, 11}; // make
  private static final String[] COLORS = {"blue", "green", "blue"};
  private static final String[] KINDS = {"arts", "albs", "tracks"};
  private static final int[][] FONT_PARAMS = {
          {20, 12, 100, 40},
          {20, 12, 40, 10},
          {18, 10, 100, 40}
  };


  private static final Map<String, Integer> TXT_MAX = new HashMap<>();
  private static final Map<String, int[]> FONTS = new HashMap<>();
  static final Map<String, String> COLOR = new HashMap<>();
  private static final Pattern REX = Pattern.compile("\\.mp3$|\\.mp4a$");
  private static Map<String, Integer> FONT_SIZES = new HashMap<>();

  static final int WIDTH = 1024;

  static void init() {
    for (int n = 0; n < KINDS.length; n++) {
      TXT_MAX.put(KINDS[n], TXT_SIZES[n]);
      //FONT.put(KINDS[n], FONT_SIZES[n]);
      COLOR.put(KINDS[n], COLORS[n]);
      FONTS.put(KINDS[n], FONT_PARAMS[n]);
    }
  }


  static Button makeButton(String file, String kind, EventHandler<ActionEvent> fun) {

    Button btn = new Button(cut(base(file), TXT_MAX.get(kind)));
    btn.setMnemonicParsing(false);
    btn.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %dpt",
        COLOR.get(kind), FONT_SIZES.get(kind)));
    btn.setOnAction(fun);
    return btn;
  }

  static void style(Node node, String kind, String color) {
    node.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %dpt",
        color, FONT_SIZES.get(kind)));
  }

  static ArrayList<String> load(String dir) {
    return new ArrayList(Arrays.stream((new File(dir)).list())
            .collect(Collectors.toList()));
  }

  static ArrayList<String> loadTracks(String artist, String album) {
    return tracks(join(artist, album));
  }

  
  static ArrayList<String> tracks(String dir) {
    ArrayList<String> files = new ArrayList<>();
    for (String item : (new File(dir)).list()) {
      String path = join(dir, item);
      if (new File(path).isDirectory())
        files.addAll(addPath(tracks(path), item));
      else if (REX.matcher(item).find())
        files.add(item);
    }
    return files;
  }
  
  static ArrayList<String> addPath(List<String> files, String dir) {
    return new ArrayList(files.stream()
            .map(f -> join(dir, f))
            .collect(Collectors.toList()));
  }


  static String cut(String name, int limit) {

    return Stream.of(name.split("[\\s+\\_+\\-+]"))
            .reduce("", (s,w) -> {
              String acc = s + " " + w;
              return acc.length() < limit ? acc : s;
            });
  }

  static String join(String dir1, String dir2, String dir3) {
    return join(dir1, join(dir2, dir3));
  }

  static String join(String dir1, String dir2) {
    return FilenameUtils.concat(dir1, dir2);
  }
  
  private static String base(String path) {
    return FilenameUtils.getBaseName(path);
  }

  static void setFontSize(String kind, ArrayList<String> items) {
    int size = items.stream().map(String::length).reduce(0, (s,i) -> s + i);
    int[] f = FONTS.get(kind);
    FONT_SIZES.put(kind, Math.max(f[0] - (size - f[2])/f[3], f[1]));
  }

}