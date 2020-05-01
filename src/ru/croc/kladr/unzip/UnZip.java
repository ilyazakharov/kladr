package ru.croc.kladr.unzip;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;

public class UnZip {

    public static void unZip7Zip(String zipFileName) {
        try {
            SevenZFile sevenZFile = new SevenZFile(new File(zipFileName));
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                System.out.println(entry.getName());
                FileOutputStream out = new FileOutputStream(entry.getName());
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                entry = sevenZFile.getNextEntry();
            }
            sevenZFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Archive was unzipped");
    }

}
