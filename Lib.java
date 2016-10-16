package gamelan;

import java.util.Map;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;

public class Lib {

  private static final Map<String, Integer> TXT_MAX = new HashMap<>();
  private static final Map<String, Integer> FONT = new HashMap<>();

  public Lib() {
    TXT_MAX.put("art", 25);
    TXT_MAX.put("alb", 40);
    TXT_MAX.put("track", 30);
    FONT.put("art", 8);
    FONT.put("alb", 10);
    FONT.put("track", 9);
  }

  public ScrollPane makeScroll(FlowPane pane) {
    ScrollPane scroll = new ScrollPane(pane);
    scroll.setPannable(true);
    scroll.setFitToWidth(true);
    return scroll;
  }


  public Button makeButton(String file, String kind, 
          EventHandler<ActionEvent> fun) {
    int max = TXT_MAX.get(kind);
    if (file.length() > max)
      file = file.substring(0, max);
    
    Button btn = new Button(file);
    btn.setStyle(String.format("-fx-text-fill: blue; -fx-font-size: %dpt",
                               FONT.get(kind)));
    btn.setOnAction(fun);
    return btn;
  }

}