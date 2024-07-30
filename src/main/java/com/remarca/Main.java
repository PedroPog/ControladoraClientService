package com.remarca;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        int port = 6001;
        FileReceiver fileReceiver;
        Thread serverThread;
        try {
            FolderWatcher folderWatcher;
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                folderWatcher = new FolderWatcher("C:\\pdvremarca\\tx");
                fileReceiver = new FileReceiver(6000,"C:\\pdvremarca\\rx");
                serverThread = new Thread(fileReceiver);
                serverThread.start();
            }else{
                String homeDireString = System.getProperty("user.home");
                folderWatcher = new FolderWatcher(homeDireString + "/pdvremarca/tx");
                fileReceiver = new FileReceiver(6000,homeDireString+"/pdvremarca/rx");
                serverThread = new Thread(fileReceiver);
                serverThread.start();
            }
            folderWatcher.startWatching();

            // Adicione um hook para parar o scheduler ao sair
            Runtime.getRuntime().addShutdownHook(new Thread(folderWatcher::stopWatching));
            try (ServerSocket serverSocket = new ServerSocket(port)) {

                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}