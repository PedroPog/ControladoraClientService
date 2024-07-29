package com.remarca;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String homeDireString = System.getProperty("user.home");
            FolderWatcher folderWatcher = new FolderWatcher(homeDireString + "/pdvremarca/tx");
            folderWatcher.startWatching();

            // Adicione um hook para parar o scheduler ao sair
            Runtime.getRuntime().addShutdownHook(new Thread(folderWatcher::stopWatching));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}