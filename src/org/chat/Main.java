package org.chat;

import javafx.collections.ListChangeListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListDataListener;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class Main extends JPanel{
    private JPanel Panel;
    private JTextField textField1;
    private JList list1;
    private JTextPane textPane1;
    private Process client;
    ChatModel model;
    Main(JFrame frame) throws IOException{
        this.client = createClient();
        InputStream inStream = client.getInputStream();
        OutputStream outStream = client.getOutputStream();
        HTMLEditorKit kit = new HTMLEditorKit();
        textPane1.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {font-family: consolas;}");
        styleSheet.addRule("div {display: block}");
        HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
        textPane1.setDocument(doc);
        this.model = new ChatModel(inStream, outStream, "Dimon666");
        model.messages.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                c.next();
                for(String m: c.getAddedSubList()) {
                    try {
                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), m+"<br>");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
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
        Main app = new Main(frame);
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
