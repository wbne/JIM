import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
public class Message
{
    private Label message;
    
    public Message(String response, String title)
    {
        Group root = new Group();
        Scene sc = new Scene(root);
        Stage popup = new Stage();
        popup.setScene(sc);
        popup.setTitle(title);
        popup.setMinHeight(200);
        popup.setMinWidth(300);
        if(response.length() == 0)
            message = new Label("Compilation Successful!");
        else
            message = new Label(response);
        message.setMinWidth(300);
        message.setTranslateX(10);
        message.setTranslateY(10);
        root.getChildren().add(message);
        
        popup.showAndWait();
    }
    
    public Message(String response)
    {
        this(response, "Compilation Results");
    }
}
