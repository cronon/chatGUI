package org.chat;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;

/**
 * Created by dima on 27/03/16.
 */
public class Main extends JPanel{
    private JPanel Panel;
    private JTextField textField1;
    private JList list1;
    private JTextArea textArea1;
    Main() throws IOException{
        InputStream stdIn = System.in;
        OutputStream stdOut = System.out;
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        System.out.println(javaBin);
        String className = Client.class.getCanonicalName();
        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className);

        Process client = builder.start();
        final InputStream inStream = client.getInputStream();
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        list1.setModel(listModel);
        new Thread(new Runnable() {
            public void run() {
                InputStreamReader reader = new InputStreamReader(inStream);
                Scanner scan = new Scanner(reader);
                while (scan.hasNextLine()) {
                    String line = scan.nextLine();
                    System.out.println(line);
                    textArea1.append(line+"\n");
                    if(line.matches("^server nicknames .*")){
                        line = line.replaceAll("^server nicknames", "");
                        String[] nickames = line.split(" ");
                        listModel.clear();
                        for(String n: nickames){
                            listModel.addElement(n);
                        }
                    }

                }
            }
        }).start();
        OutputStream outStream = client.getOutputStream();
        PrintWriter out = new PrintWriter(outStream);
        send(out, "connect XX");
        send(out, "server nicknames");
//        pWriter.close();
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == 10){
                    send(out, textField1.getText());
                    textField1.setText("");
                }
            }
        });
    }
    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().Panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
    static void send(PrintWriter out, String text){
        out.println(text);
        out.flush();
    }
}
