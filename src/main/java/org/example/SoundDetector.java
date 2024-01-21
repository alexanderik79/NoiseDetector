package org.example;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SoundDetector {
    private static final int DETECTION_INTERVAL = 10;
    private static final int DETECTION_FLOOR = 7;
    private static final int DETECTION_LEVEL = 14;
    private static final Long RECEIVER = 979696456L;
    private static final double LATITUDE = 40.0000000; //координати конкретного телефона слухача
    private static final double LONGITUDE = 3.6813000;
    public static void detect() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Listening for sound...");

            byte[] buffer = new byte[4096];
            int bytesRead;

//            double[] record = new double[DETECTION_INTERVAL];
            int indexToFill = 0;

            while (true) {
                bytesRead = line.read(buffer, 0, buffer.length);
                // перевірка, чи середнє значення байтів перевищує певний поріг
                double average = calculateAverage(buffer, bytesRead);
                if (average > DETECTION_LEVEL) {
                   // record[indexToFill] = average;
                    indexToFill++;

                    System.out.print(".");
                    if(indexToFill == DETECTION_INTERVAL){
                        System.out.println("ALARM!!!!!!!!");

                        try {
                            TelegramSender.sendLocation(RECEIVER, LATITUDE, LONGITUDE);
                        } catch (IOException e) {
                            System.out.println("error");
                        }

//                        Arrays.fill(record, 0.0);
                        indexToFill = 0;
                    }
                }
                if (average < DETECTION_FLOOR) {
//                    Arrays.fill(record, 0.0);
                    indexToFill = 0;
                    System.out.print("_");
                }
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // метод вираховує середнє значення гучності
    private static double calculateAverage(byte[] buffer, int bytesRead) {
        long sum = 0;
        for (int i = 0; i < bytesRead; i++) {
            sum += Math.abs(buffer[i]);
        }
        return (double) sum / bytesRead;
    }

    private static void writeBufferToFile(byte[] buffer, String fileName) {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

        try {
            // Создаем поток для записи в файл
            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(buffer), format, buffer.length);

            File audioFile = new File(fileName);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);

            System.out.println("Audio recording complete. File saved to: " + audioFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
