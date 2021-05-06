import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.text.Font;
public class Terminal
{
    private Label results;
    private TextField input;
    
    public Terminal()
    {
        Group root = new Group();
        Scene sc = new Scene(root);
        Stage popup = new Stage();
        popup.setScene(sc);
        popup.setTitle("JIM's Terminal");
        popup.setWidth(300);
        popup.setHeight(600);
        
        Font defaultFont = new Font("Consolas", 12);
        
        results = new Label();
        results.setMinWidth(200);
        results.setFont(defaultFont);
        root.getChildren().add(results);
        
        input = new TextField();
        input.setMinWidth(200);
        input.setFont(defaultFont);
        root.getChildren().add(input);
        input.setTranslateY(500);
        
        popup.show();
    }
    
    public void addText(String text)
    {
        results.setText(results.getText() + text);
    }
    
    public void clearText()
    {
        results.setText("");
    }
}
