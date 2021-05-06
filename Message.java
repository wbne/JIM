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
    
    public Message(String response)
    {
        Group root = new Group();
        Scene sc = new Scene(root);
        Stage popup = new Stage();
        popup.setScene(sc);
        popup.setTitle("Compilation Results");
        popup.setMinHeight(200);
        popup.setMinWidth(300);
        if(response.length() == 0)
            message = new Label("Compilation Successful!");
        else
            message = new Label(response);
        message.setMinWidth(300);
        root.getChildren().add(message);
        
        popup.showAndWait();
    }
}
