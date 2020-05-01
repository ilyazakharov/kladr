package ru.croc.kladr.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadHTTP {

    public static void downloadKladr(String zipName) {

        try {
            URL website = new URL("http://gnivc.ru/html/gnivcsoft/KLADR/Base.7z");
            ReadableByteChannel rbc = null;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(zipName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File was downloaded");
    }
}
