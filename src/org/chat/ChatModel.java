package org.chat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Scanner;

public class ChatModel {
    Scanner input;
    PrintWriter output;
    public ObservableList<String> nicknames = FXCollections.observableArrayList();
    public ObservableList<String> messages = FXCollections.observableArrayList();
    public ChatModel(InputStream input, OutputStream output, String nickname){
        this.input = new Scanner(new InputStreamReader(input));
        this.output = new PrintWriter(output);
        send(Protocol.connect(nickname));
        new Thread(() -> {
            String result;
            while (this.input.hasNextLine()) {
                String line = this.input.nextLine();
                System.out.println(line);
                result = Protocol.processInput(line, nicknames, messages);
                if(result != null){
                    send(result);
                }
            }
        }).start();
    }
    void send(String message){
        System.out.println(message);
        this.output.println(message);
        this.output.flush();
    }
}
