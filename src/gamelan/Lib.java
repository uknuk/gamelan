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

  static final Map<String, Integer> TXT_MAX = new HashMap<>();
  static final Map<String, Integer> FONT = new HashMap<>();
  static final Map<String, String> COLOR = new HashMap<>();
  static final Pattern REX = Pattern.compile("\\.mp3$|\\.mp4a$");

  static void init() {
    TXT_MAX.put("arts", 25);
    TXT_MAX.put("albs", 40);
    TXT_MAX.put("tracks", 30);
    FONT.put("arts", 8);
    FONT.put("albs", 10);
    FONT.put("tracks", 9);
    COLOR.put("arts", "blue");
    COLOR.put("albs", "green");
    COLOR.put("tracks", "blue");
  }


  static Button makeButton(String file, String kind,
          EventHandler<ActionEvent> fun) {
    
    String name = base(file);
    
    int max = TXT_MAX.get(kind);
    if (name.length() > max)
      name = name.substring(0, max);
    
    Button btn = new Button(name);
    btn.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %dpt",
        COLOR.get(kind), FONT.get(kind)));
    btn.setOnAction(fun);
    return btn;
  }
  
  static ArrayList<String> tracks(String dir) {
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
  
  static String join(String dir1, String dir2) {
    return FilenameUtils.concat(dir1, dir2);
  }
  
  static String base(String path) {
    return FilenameUtils.getBaseName(path);
  }

}