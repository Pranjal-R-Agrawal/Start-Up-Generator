package view;

import app.SignupUseCaseFactory;
import data_access.MongoDBDataAccessObject;
import data_access.MongoDBDataAccessObjectBuilder;

import javax.swing.*;
import java.awt.*;

public class NewWindow {
    private JPanel view;
    private final boolean scrollable;
    private String viewName;
    private JFrame frame = null;
    public NewWindow(boolean scrollable, String viewName){
        this.scrollable = scrollable;
        this.viewName = viewName;
    }
    public void setView(JPanel view){
        this.view = view;
    }
    public void setViewName(String viewName){this.viewName = viewName;}
    public void createWindow(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (scrollable){
            frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(view);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            frame.getContentPane().add(scrollPane);
        } else {
            frame.add(view);
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public void closeWindow(){
        frame.setVisible(false);
        frame.dispose();
    }
    public void resize(){
        frame.pack();
    }
}
