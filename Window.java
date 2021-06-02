import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
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
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.awt.event.*;

public class Window extends Application
{
    private boolean writingMode = false;
    private Label clonedText;
    private TextArea codingArea;
    private Rectangle cursor;
    private int startX = 7, startY = 3;
    private String fileDirectory;
    private ArrayList<File> files = new ArrayList<File>();
    private String filename;
    private File currentFile;
    private String scanBuffer;
    private String directory = "";
    private final String INSTRUCTIONS = "INSTRUCTIONS\ne: edit mode\nijkl: arrow keys\nc: compile\ns: save\nb: open\nh: help\nqq: quit\nesc: exit edit mode";
    private int quitCount = 0;
    
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
        codingArea.setPromptText(INSTRUCTIONS);
        String charTyped = "";
        codingArea.setOnKeyTyped(event -> {
                if(event.getCharacter().equals("\r")){
                    int tabCount = returnTabCount();
                    for(int i = 0; i < tabCount; i++)
                        codingArea.insertText(codingArea.getCaretPosition(), "\t");
                }    
                codingArea.selectForward();
                String buffer = codingArea.getSelectedText();
                codingArea.deselect();
                if(!buffer.equals(""))
                    left();
                if(event.getCharacter().equals("{"))
                {
                    codingArea.insertText(codingArea.getCaretPosition(), "}");
                    left();
                }
                if(event.getCharacter().equals("}"))
                {
                    if(buffer.equals("}"))
                        codingArea.deleteNextChar();
                }
                if(event.getCharacter().equals("["))
                {
                    codingArea.insertText(codingArea.getCaretPosition(), "]");
                    left();
                }
                if(event.getCharacter().equals("]"))
                {
                    if(buffer.equals("]"))
                        codingArea.deleteNextChar();
                }
                if(event.getCharacter().equals("("))
                {
                    codingArea.insertText(codingArea.getCaretPosition(), ")");
                    codingArea.backward();
                }
                if(event.getCharacter().equals(")"))
                {
                    if(buffer.equals(")"))
                        codingArea.deleteNextChar();
                }
                if(event.getCharacter().equals("\""))
                {
                    if(buffer.equals("\""))
                        codingArea.deleteNextChar();
                    else
                    {
                        codingArea.insertText(codingArea.getCaretPosition(), "\"");
                        left();
                    }
                }
                if(event.getCharacter().equals("\'"))
                {
                    if(buffer.equals("\'"))
                        codingArea.deleteNextChar();
                    else
                    {
                        codingArea.insertText(codingArea.getCaretPosition(), "\'");
                        left();
                    }
                }
            });

        clonedText = new Label(INSTRUCTIONS);
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
                    if(!writingMode && e.getCode() == KeyCode.E)
                    {
                        toggleMode();
                    }

                    if(!writingMode && e.getCode() == KeyCode.J)
                    {
                        left();
                    }
                    if(!writingMode && e.getCode() == KeyCode.K)
                    {
                        down();
                    }
                    if(!writingMode && e.getCode() == KeyCode.I)
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
                            insertNewline();
                            up();
                        }
                        else
                        {
                            insertNewline();
                        }
                    }
                    if(!writingMode && e.getCode() == KeyCode.S)
                    {
                        try{
                            save(stage);
                        }
                        catch(IOException ioe)
                        {
                            System.out.println("error");
                        }
                    }
                    if(!writingMode && e.getCode() == KeyCode.C)
                    {
                        try{
                            compile(save(stage));
                        }
                        catch(Exception eee)
                        {
                            System.out.println("error");
                        }
                    }
                    if(!writingMode && e.getCode() == KeyCode.B)
                    {
                        load(stage);
                    }
                    if(!writingMode && e.getCode() == KeyCode.H)
                    {
                        help();
                    }
                    if(!writingMode && e.getCode() == KeyCode.Q)
                    {
                        quit(stage);
                    }
                    else if(quitCount != 0)
                    {
                        quitCount = 0;
                    }
                }
            });

        stage.show();
    }

    private void toggleMode()
    {
        writingMode = !writingMode;
        codingArea.setEditable(writingMode);
        String text = codingArea.getText();
        clonedText.setText(text);
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
        char[] tempText = codingArea.getText().toCharArray();
        int position = codingArea.getCaretPosition();
        int lines = 0;
        int charSum = -1;
        int numTabs = 0;
        while(position > tokens[lines].length())
        {
            charSum += (tokens[lines].length() + 1);
            position -= (tokens[lines++].length() + 1);
            if(lines >= tokens.length)
                break;
        }
        for(int i = 1; i <= position; i++)
            if(tempText[charSum + i] == '\t')
                numTabs++;
        cursor.setX(startX + 6.6 * position + 6.6 * 7 * numTabs);
        cursor.setY(startY + 16 * lines);
    }

    private void insertNewline()
    {
        int tabCount = returnTabCount();
        codingArea.insertText(codingArea.getCaretPosition(), "\n");
        for(int i = 0; i < tabCount; i++)
            codingArea.insertText(codingArea.getCaretPosition(), "\t");

        toggleMode();
    }

    private int save(Stage stage) throws IOException
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Java", "*.java"));
        currentFile = fileChooser.showSaveDialog(stage);
        directory = currentFile.getParent();
        if(currentFile == null)
            return -1;
        filename = currentFile.getName();
        if (currentFile.createNewFile()) {} 
        else {
            currentFile.delete();
            currentFile.createNewFile();
        }
        FileWriter myWriter = new FileWriter(currentFile);
        myWriter.write(codingArea.getText());
        myWriter.close();
        return 1;
    }

    private void compile(int response) throws Exception
    {
        if(response == -1)
            return;
        if(!files.isEmpty())
        {
            files.clear();
            files.add(currentFile);
        }
        else
        {
            files.add(currentFile);
        }

        ByteArrayOutputStream error = new ByteArrayOutputStream();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, error, "-proc:none", files.get(0).toString());
        Message compilationResult = new Message(error.toString());
        if(result != 0)
            return;

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-proc:none"), null, fileManager.getJavaFileObjectsFromFiles(files));
        if(!task.call())
        {System.out.println("F");}
        fileManager.close();

        String noExtension = filename.substring(0,filename.length() - 5);
        Runtime re = Runtime.getRuntime();
        String command = "cmd /c start cmd.exe /K \"cd "+ directory +"&& java " + noExtension +"\"";
        Process p = re.exec(command);
        p.waitFor();
    }

    private void load(Stage stage)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Java", "*.java"));
        currentFile = fileChooser.showOpenDialog(stage);
        if(currentFile == null)
            return;
        Scanner sc = new Scanner(System.in);
        try{sc = new Scanner(currentFile);}
        catch(Exception eae){System.out.println("error");}
        String tempText = "";
        while(sc.hasNext())
        {
            tempText += (sc.nextLine() + "\n");
        }
        codingArea.setText(tempText);
        toggleMode();
        toggleMode();
    }

    private int returnTabCount()
    {
        int tabCount = 0;
        int lineCount = 1;
        int position = codingArea.getCaretPosition();
        char[] tempText = codingArea.getText().substring(0, position).toCharArray();
        for(char c : tempText)
            if(c == '\n')
                lineCount++;
        //System.out.println("line count:"+lineCount + " position: " + position);
        for(int i = position - lineCount; i > 0; i--)
        {
            char c = tempText[i];
            if(c == '\t')
                tabCount++;
            if(c == '\n')
                break;
        }
        return tabCount;
    }
    
    private void help()
    {
        new Message(INSTRUCTIONS, "Help");
    }
    
    private void quit(Stage stage)
    {
        quitCount++;
        if(quitCount == 2)
            try{stage.close();}catch(Exception e){}
    }
}
