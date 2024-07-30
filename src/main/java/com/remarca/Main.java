package com.remarca;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 6001;
    private static final String ICON_PATH = "assets/logo.png"; // Substitua pelo caminho para o ícone

    public static void main(String[] args) {
        if (SystemTray.isSupported()) {
            createSystemTrayIcon();
        } else {
            System.err.println("System tray not supported!");
        }

        // Configure o thread de fundo para monitorar a pasta e o servidor
        new Thread(() -> {
            try {
                FolderWatcher folderWatcher;
                FileReceiver fileReceiver;
                Thread serverThread;

                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    folderWatcher = new FolderWatcher("C:\\pdvremarca\\tx");
                    fileReceiver = new FileReceiver(6000, "C:\\pdvremarca\\rx");
                } else {
                    String homeDirString = System.getProperty("user.home");
                    folderWatcher = new FolderWatcher(homeDirString + "/pdvremarca/tx");
                    fileReceiver = new FileReceiver(6000, homeDirString + "/pdvremarca/rx");
                }

                serverThread = new Thread(fileReceiver);
                serverThread.start();
                folderWatcher.startWatching();

                // Adicione um hook para parar o scheduler ao sair
                Runtime.getRuntime().addShutdownHook(new Thread(folderWatcher::stopWatching));

                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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
        }).start();
    }

    private static void createSystemTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(ICON_PATH);

            TrayIcon trayIcon = new TrayIcon(image, "Remarca App");
            trayIcon.setImageAutoSize(true);

            // Adiciona um menu de contexto ao ícone da bandeja
            PopupMenu popupMenu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Sair");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Fecha a aplicação
                }
            });
            popupMenu.add(exitItem);
            trayIcon.setPopupMenu(popupMenu);

            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
