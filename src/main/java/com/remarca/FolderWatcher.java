package com.remarca;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class FolderWatcher {
    private final WatchService watchService;
    private final Path pathToWatch;

    public FolderWatcher(String folderPath) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.pathToWatch = Paths.get(folderPath);
        this.pathToWatch.register(watchService,StandardWatchEventKinds.ENTRY_CREATE);
    }

    public void watchFolder() {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                System.out.println("Novo arquivo detectado: " + fileName);
                // Aqui você chamaria a função para enviar o arquivo
                sendFileToIP(fileName.toString());
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public void sendFileToIP(String filePath) {
        String serverIP = "192.168.1.100"; // Substitua pelo IP desejado
        int serverPort = 6000; // Substitua pela porta desejada

        File file = new File(filePath);
        try (Socket socket = new Socket(serverIP, serverPort);
             FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             OutputStream os = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(os, true)) {

            // Enviar o nome do arquivo primeiro
            pw.println(file.getName());

            byte[] fileBytes = new byte[(int) file.length()];
            bis.read(fileBytes, 0, fileBytes.length);
            os.write(fileBytes, 0, fileBytes.length);
            os.flush();
            System.out.println("Arquivo enviado para " + serverIP + ":" + serverPort);

            // Apagar o arquivo após o envio
            if (file.delete()) {
                System.out.println("Arquivo deletado: " + filePath);
            } else {
                System.out.println("Falha ao deletar o arquivo: " + filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
