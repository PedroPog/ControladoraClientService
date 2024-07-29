package com.remarca;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FileReceiver fileReceiver = new FileReceiver(6000, "/home/desenvolvimento/pdvremarca/rx"); // Ajuste o caminho do diretório conforme necessário
        fileReceiver.startServer();
        /*try {
            String homeDireString = System.getProperty("user.home");
            FolderWatcher folderWatcher =
                    new FolderWatcher(homeDireString+"/pdvremarca/tx");
            folderWatcher.watchFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}