package gamelan;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Lib {

  static final int[] TXT_SIZES = {25, 40, 30};
  static final int[] FONT_SIZES = {10, 14, 12}; // make dynamic
  static final String[] COLORS = {"blue", "green", "blue"};
  static final String[] KINDS = {"arts", "albs", "tracks"};
  static final Map<String, Integer> TXT_MAX = new HashMap<>();
  static final Map<String, Integer> FONT = new HashMap<>();
  static final Map<String, String> COLOR = new HashMap<>();
  static final Pattern REX = Pattern.compile("\\.mp3$|\\.mp4a$");

  static final int WIDTH = 1024;

  static void init() {
    for (int n = 0; n < KINDS.length; n++) {
      TXT_MAX.put(KINDS[n], TXT_SIZES[n]);
      FONT.put(KINDS[n], FONT_SIZES[n]);
      COLOR.put(KINDS[n], COLORS[n]);
    }
   /* TXT_MAX.put("arts", 25);
    TXT_MAX.put("albs", 40);
    TXT_MAX.put("tracks", 30);
    FONT.put("arts", 8);
    FONT.put("albs", 10);
    FONT.put("tracks", 9);
    COLOR.put("arts", "blue");
    COLOR.put("albs", "green");
    COLOR.put("tracks", "blue");*/

  }


  static Button makeButton(String file, String kind,
          EventHandler<ActionEvent> fun) {
    
    String name = base(file);
    
    int max = TXT_MAX.get(kind);
    if (name.length() > max)
      name = name.substring(0, max);
    
    Button btn = new Button(name);
    btn.setMnemonicParsing(false);
    btn.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %dpt",
        COLOR.get(kind), FONT.get(kind)));
    btn.setOnAction(fun);
    return btn;
  }
  
  static ArrayList<String> tracks(String artist, String album) {
    String dir = join(artist, album);
    return new ArrayList(load(dir).stream()
            .filter(f -> REX.matcher(f).find())
            .collect(Collectors.toList()));
    
  }
  
  static ArrayList<String> load(String dir) {
    return new ArrayList(Arrays.stream((new File(dir)).list())
                .collect(Collectors.toList()));
  }
  
  static ArrayList<String> addPath(List<String> files, String dir) {
    return new ArrayList(files.stream()
            .map(f -> join(dir, f))
            .collect(Collectors.toList()));
  }

  
  static String join(String dir1, String dir2, String dir3) {
    return join(dir1, join(dir2, dir3));
  }

  static String join(String dir1, String dir2) {
    return FilenameUtils.concat(dir1, dir2);
  }
  
  static String base(String path) {
    return FilenameUtils.getBaseName(path);
  }


}