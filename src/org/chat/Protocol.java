package org.chat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Protocol {
    static String connect(String nickname){
        return "connect " + nickname;
    }
    static String disconnect(){
        return "server disconnect";
    }
    static String processInput(String input, List<String> nicknames, List<String> messages){
        if(input.matches("^server .*")){
            return server(input.replaceFirst("^server ", ""), nicknames, messages);
        } else if(input.matches("^chat .*")){
            chat(input.replaceFirst("^chat ", ""), messages);
            return null;
        } else {
            return unrecognized(input);
        }
    }
    static String processOutput(String message){
        if(message.matches("^/.*")){
            return command(message.substring(0));
        } else {
            return "chat " + message;
        }
    }
    static String command(String command){
        return "";
    }
    static void chat(String input, List<String> messages){
        String line = input.replaceFirst("(^\\S+)", "<$1>");
        messages.add(timestamp(line));
    }
    static String timestamp(String s){
        SimpleDateFormat formatter = new SimpleDateFormat("/HH:mm:ss/");
        String date = formatter.format(new Date());
        return date + " " + s;
    }
    static String server(String input, List<String> nicknames, List<String> messages) {
        if(input.matches("^nicknames .*")){
            updateNicknames(input.replaceFirst("^nicknames ", ""), nicknames);
            return null;
        } else if(input.matches("^nickname .*")){
            return requestNicknames();
        } else if(input.matches("^connect .*")){
            return requestNicknames();
        } else if(input.matches("^disconnect .*")) {
            return requestNicknames();
        } else if(input.matches("^error .*")){
            return error(input.replace("^error ", ""), messages);
        } else {
            return unrecognized("server " + input);
        }
    }
    static String error(String input, List<String> messages){
        messages.add(input);
        return null;
    }
    static void updateNicknames(String input, List<String> nicknames){
        nicknames.clear();
        nicknames.addAll(Arrays.asList(input.split(" ")));
    }
    static String requestNicknames(){
        return "server nicknames";
    }
    static String unrecognized(String input){
        System.out.println(timestamp("Server has sent something weird:"));
        System.out.println(input);
        return null;
    }
}
