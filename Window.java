import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.tools.*;
import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class Window extends Application
{
    private boolean writingMode = false;
    private Label clonedText;
    private TextArea codingArea;
    private Rectangle cursor;
    private int startX = 7, startY = 3;
    private String fileDirectory;
    private ArrayList<File> files = new ArrayList<File>();

    public static void main(String []args)
    {
        launch(args);
    }

    public void start(Stage stage)
    {
        stage.setTitle("JIM - Java Compiler + VIM");
        Group root = new Group();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);

        Font defaultFont = new Font("Consolas", 12);

        codingArea = new TextArea();
        codingArea.setMinWidth(800);
        codingArea.setMinHeight(600);
        root.getChildren().add(codingArea);
        codingArea.setEditable(writingMode);
        codingArea.setVisible(writingMode);
        codingArea.setFont(defaultFont);

        clonedText = new Label("");
        clonedText.setMinWidth(800);
        root.getChildren().add(clonedText);
        clonedText.setTranslateX(8);
        clonedText.setTranslateY(4);
        clonedText.setVisible(!writingMode);
        clonedText.setFont(defaultFont);

        cursor = new Rectangle();
        cursor.setX(startX); //each character is 6.6 pixels wide at 12 font
        cursor.setY(startY); //each character is 16 pixels high at 12 font
        cursor.setWidth(1);
        cursor.setHeight(17);
        root.getChildren().add(cursor);
        cursor.setVisible(!writingMode);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
                @Override
                public void handle(KeyEvent e)
                {
                    if(writingMode && e.getCode() == KeyCode.ESCAPE)
                    {
                        toggleMode();
                    }
                    if(!writingMode && e.getCode() == KeyCode.I)
                    {
                        toggleMode();
                    }

                    if(!writingMode && e.getCode() == KeyCode.H)
                    {
                        left();
                    }
                    if(!writingMode && e.getCode() == KeyCode.J)
                    {
                        down();
                    }
                    if(!writingMode && e.getCode() == KeyCode.K)
                    {
                        up();
                    }
                    if(!writingMode && e.getCode() == KeyCode.L)
                    {
                        right();
                    }

                    if(!writingMode && e.getCode() == KeyCode.O)
                    {
                        if(e.isShiftDown())
                        {
                            up();
                            insertNewline();
                        }
                        else
                        {
                            insertNewline();
                        }
                    }
                    if(!writingMode && e.getCode() == KeyCode.S)
                    {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save");
                        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Java", "*.java"));
                        files.add(fileChooser.showSaveDialog(stage));
                        //String[] temp = files.get(0).toString().split("\\");
                        //String filename = temp[temp.length - 1];
                        //System.out.println(filename);
                        try {
                            if (files.get(0).createNewFile()) {} else {
                                files.get(0).delete();
                                files.get(0).createNewFile();
                            }
                            try {
                                FileWriter myWriter = new FileWriter("HelloWorld.java");
                                myWriter.write(codingArea.getText());
                                myWriter.close();
                            } catch (IOException ewe) {
                                ewe.printStackTrace();
                            }
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }
                    }
                    if(!writingMode && e.getCode() == KeyCode.C)
                    {
                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        //String[] temp = files.get(0).toString().split("\\");
                        //String filename = temp[temp.length - 1];
                        //System.out.println(filename);
                        int result = compiler.run(null, null, null, "-proc:none", files.get(0).toString());
                        String dir = files.get(0).getParent();
                        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
                        CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-proc:none"), null, fileManager.getJavaFileObjectsFromFiles(files));
                        if(!task.call())
                        {System.out.println("F");}
                        try{fileManager.close();}catch(Exception owo){}
                    }
                }
            });

        stage.show();
    }

    private void toggleMode()
    {
        writingMode = !writingMode;
        codingArea.setEditable(writingMode);
        clonedText.setText(codingArea.getText());
        codingArea.setVisible(writingMode);
        clonedText.setVisible(!writingMode);
        cursor.setVisible(!writingMode);
        updateCursor();
    }

    private void up()
    {
        String[] tokens = codingArea.getText().split("\n");
        int position = codingArea.getCaretPosition();
        int lines = 0;
        int summation = 0;
        while(position > tokens[lines].length())
        {
            if(lines != 0)
                summation += (tokens[lines - 1].length() + 1);
            position -= (tokens[lines++].length() + 1);
            if(lines >= tokens.length)
                break;
        }
        if(lines == 0)
            codingArea.positionCaret(0);
        else if(tokens[lines - 1].length() > position)
            codingArea.positionCaret(summation + position);
        else
            codingArea.positionCaret(summation + tokens[lines - 1].length());
        updateCursor();
    }

    private void down()
    {
        String[] tokens = codingArea.getText().split("\n");
        int position = codingArea.getCaretPosition();
        int lines = 0;
        int summation = 0;
        while(position > tokens[lines].length())
        {
            summation += (tokens[lines].length() + 1);
            position -= (tokens[lines++].length() + 1);
            if(lines >= tokens.length)
                break;
        }
        if(lines < tokens.length)
            summation += (tokens[lines].length() + 1);
        if(lines == tokens.length - 1)
            codingArea.positionCaret(summation);
        else
            codingArea.positionCaret(summation + position);
        updateCursor();
    }

    private void left()
    {
        codingArea.positionCaret(codingArea.getCaretPosition() - 1);
        updateCursor();
    }

    private void right()
    {
        codingArea.positionCaret(codingArea.getCaretPosition() + 1);
        updateCursor();
    }

    private void updateCursor()
    {
        String[] tokens = codingArea.getText().split("\n");
        int position = codingArea.getCaretPosition();
        int lines = 0;
        while(position > tokens[lines].length())
        {
            position -= (tokens[lines++].length() + 1);
            if(lines >= tokens.length)
                break;
        }
        cursor.setX(startX + 6.6 * position);
        cursor.setY(startY + 16 * lines);
    }

    private void insertNewline()
    {
        codingArea.insertText(codingArea.getCaretPosition(), "\n");
        toggleMode();
    }
}
