import filestreams.GameProgress;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Tegneryadnov_VI
 * @version 21
 * @Java Сохранение
 */

public class Main {
    private final static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        final File storageFolder = new File("C:/temp/Game/savegames");
        try {
            saveGames(storageFolder, new GameProgress(100, 80, 1, 10));
            saveGames(storageFolder, new GameProgress(90, 70, 2, 25));
            saveGames(storageFolder, new GameProgress(40, 30, 3, 15));

            String[] fullFileNames = Arrays.stream(storageFolder.listFiles())
                    .map(x -> x.getAbsolutePath())
                    .collect(Collectors.toList())
                    .toArray(new String[0]);

            zipFiles(String.format("%s/%s", storageFolder.getPath(), "zip.zip"),
                    fullFileNames);

            toLog(String.format("Файлы в папке %s помещены в архив %s\\zip.zip", storageFolder.getPath(), storageFolder.getPath()));

            for (String fullFileName : fullFileNames) {
                File tmpFile = new File(fullFileName);
                tmpFile.delete();
            }
            toLog(String.format("Из папки %s удалены файлы сохранения игры", storageFolder.getPath()));
        } finally {
            logToFile("C:/temp/Game/temp/temp.txt");
        }
    }

    public static void saveGames(File storageFolder, GameProgress snapShotGame) {
        long fileIndex = Arrays.stream(storageFolder.list()).filter(x -> x.contains("save") && x.contains(".dat")).count();
        try (FileOutputStream fos = new FileOutputStream(String.format("%s/%s%d.dat", storageFolder.getPath(), "save", ++fileIndex));
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(snapShotGame);
            toLog(String.format("Игра сохранена в %s", String.format("%s\\%s%d.dat", storageFolder.getPath(), "save", fileIndex)));
        } catch (Exception ex) {
            toLog("Ошибка сохранения игры: " + ex.getMessage());
        }
    }

    public static void zipFiles(String zipFullFileName, String[] zippedFullFileNames) {
        try (ZipOutputStream zout = new ZipOutputStream(new
                FileOutputStream(zipFullFileName));
        ) {
            for (String fullFileName : zippedFullFileNames) {
                try (
                        FileInputStream fis = new FileInputStream(fullFileName)) {
                    ZipEntry entry = new ZipEntry(fullFileName.replaceFirst("^\\S*\\\\", ""));
                    zout.putNextEntry(entry);
                    // считываем содержимое файла в массив byte
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    // добавляем содержимое к архиву
                    zout.write(buffer);
                    // закрываем текущую запись для новой записи
                    zout.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void toLog(String message) {
        System.out.println(message);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        log.append(String.format("%s: %s\n", LocalDateTime.now().format(formatter), message));
    }

    public static void logToFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(log.toString());
        } catch (IOException ex) {
            System.out.printf("Ошибка сохранения лога %s:", ex.getStackTrace());
        }
    }
}
