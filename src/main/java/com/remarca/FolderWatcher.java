package com.remarca;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderWatcher {
    private final WatchService watchService;
    private final Path pathToWatch;
    private final ScheduledExecutorService scheduler;

    public FolderWatcher(String folderPath) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.pathToWatch = Paths.get(folderPath);
        this.pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startWatching() {
        System.out.println("Aplicação iniciada. Verificação de arquivos a cada 30 segundos.");
        scheduler.scheduleAtFixedRate(this::watchFolder, 0, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::warnBeforeCheck, 20, 30, TimeUnit.SECONDS);
    }

    public void stopWatching() {
        scheduler.shutdown();
    }

    public void sendFile(String filePath){
        File file = new File(filePath);
        if(!file.exists()||!file.isFile()){
            System.out.println("Arquivo não encontrado: "+filePath);
            return;
        }
        handleFile(filePath);
    }

    private void handleFile(String filePath) {
        String nomeValue = extractNomeValue(filePath);
        if (nomeValue != null) {
            String serverIP = "192.168.10.16"; // Substitua pelo IP desejado
            String finalDestination = serverIP + "/" + nomeValue;
            System.out.println("Enviando para: " + finalDestination);
            sendFileToIP(finalDestination);
        } else {
            System.out.println("Não foi possível extrair o valor do NOME.");
        }
    }

    private String extractNomeValue(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Pattern pattern = Pattern.compile("NOME=\"[^\"]+/([^\"]+)\"");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void warnBeforeCheck() {
        System.out.println("Faltam 10 segundos para a próxima verificação de arquivos.");
    }

    private void watchFolder(){
        WatchKey key;
        try {
            key = watchService.poll();
            if (key == null) {
                return;
            }
        } catch (ClosedWatchServiceException ex) {
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
            ClearConsole.clear();
            sendFileToIP(pathToWatch.resolve(fileName).toString());
        }

        boolean valid = key.reset();
        if (!valid) {
            stopWatching();
        }
    }

    private void sendFileToIP(String filePath) {
        String serverIP = "192.168.10.16"; // Substitua pelo IP desejado
        int serverPort = 6000; // Substitua pela porta desejada

        File file = new File(filePath);
        try (Socket socket = new Socket(serverIP, serverPort);
             FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             OutputStream os = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(os, true)) {

            pw.println(file.getName());

            byte[] fileBytes = new byte[(int) file.length()];
            bis.read(fileBytes, 0, fileBytes.length);
            os.write(fileBytes, 0, fileBytes.length);
            os.flush();
            System.out.println("Arquivo enviado para " + serverIP + ":" + serverPort);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file.delete()) {
                System.out.println("Arquivo deletado: " + filePath);
            } else {
                System.out.println("Falha ao deletar o arquivo: " + filePath);
            }
        }
    }
}