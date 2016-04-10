package org.chat;

import javafx.collections.ListChangeListener;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import java.awt.event.*;
import java.io.*;
import java.util.List;

public class Main extends JPanel{
    private JPanel Panel;
    private JTextField textField1;
    private JList list1;
    private JTextArea textArea1;
    private Process client;
    ChatModel model;
    Main() throws IOException{
        this.client = createClient();
        InputStream inStream = client.getInputStream();
        OutputStream outStream = client.getOutputStream();

        this.model = new ChatModel(inStream, outStream, "Dimon666");
        model.messages.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                c.next();
                for(String s: c.getAddedSubList()){
                    textArea1.append(s+"\n");
                }
            }
        });

        DefaultListModel<String> listModel = new DefaultListModel<>();
        list1.setModel(listModel);
        model.nicknames.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                listModel.clear();
                model.nicknames.forEach(listModel::addElement);
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == 10){
                    model.send(Protocol.processOutput(textField1.getText()));
                    textField1.setText("");
                }
            }
        });
    }
    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("Main");
        Main app = new Main();
        frame.setContentPane(app.Panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.model.send(Protocol.disconnect());
            }
        });
    }
    private static Process createClient() throws IOException{
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = Client.class.getCanonicalName();
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
        return builder.start();
    }
}
