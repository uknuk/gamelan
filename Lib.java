package gamelan;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import org.apache.commons.io.FilenameUtils;

public class Lib {

  private static final Map<String, Integer> TXT_MAX = new HashMap<>();
  private static final Map<String, Integer> FONT = new HashMap<>();
  private static final Pattern rex = Pattern.compile("\\.mp3$|\\.mp4a$");

  public Lib() {
    TXT_MAX.put("arts", 25);
    TXT_MAX.put("albs", 40);
    TXT_MAX.put("tracks", 30);
    FONT.put("arts", 8);
    FONT.put("albs", 10);
    FONT.put("tracks", 9);
  }

  public ScrollPane makeScroll(FlowPane pane) {
    ScrollPane scroll = new ScrollPane(pane);
    scroll.setPannable(true);
    scroll.setFitToWidth(true);
    return scroll;
  }


  public Button makeButton(String file, String kind, 
          EventHandler<ActionEvent> fun) {
    if (kind.equals("selAlbs"))
      kind = "albs";
    
    file = base(file);
    
    int max = TXT_MAX.get(kind);
    if (file.length() > max)
      file = file.substring(0, max);
    
    Button btn = new Button(file);
    btn.setStyle(String.format("-fx-text-fill: blue; -fx-font-size: %dpt",
                               FONT.get(kind)));
    btn.setOnAction(fun);
    return btn;
  }
  
  List<String> tracks(String dir) {
    return load(dir).stream()
            .filter(f -> rex.matcher(f).find())
            .collect(Collectors.toList());
    
  }
  
  List<String> load(String dir) {
    return Arrays.stream((new File(dir)).list())
                .collect(Collectors.toList());
  }
  
  List<String> addPath(List<String> files, String dir) {
    return files.stream()
            .map(f -> join(dir, f))
            .collect(Collectors.toList());
  }
  
  String join(String dir1, String dir2) {
    return FilenameUtils.concat(dir1, dir2);
  }
  
  String base(String path) {
    return FilenameUtils.getBaseName(path);
  }

}