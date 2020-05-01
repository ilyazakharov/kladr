package ru.croc.kladr;

import ru.croc.kladr.dbf2csv.DbfToCsv;
import ru.croc.kladr.download.DownloadHTTP;
import ru.croc.kladr.unzip.UnZip;
import java.io.File;

public class RunKladrRoutines {
    public static void main(String[] args) {
        String dir = System.getProperty("user.dir"); // Текущая директория
        String zipName = "Base.7z"; // имя архива
        String fullName = dir + "/" + zipName;
        File dirF = new File(dir);

        DownloadHTTP.downloadKladr(fullName); // Скачиваем архив
        UnZip.unZip7Zip(fullName); // Распаковываем архив

        File archive = new File(fullName);
        if (archive.exists()) {
            archive.delete(); // Удаляем архив
        }
        for(File item : dirF.listFiles()) {
            if (getFileExtension(item).equals("dbf")) {
                String dbf = item.getPath();
                String outputCSV =dbf.substring(0, dbf.lastIndexOf(".")) + ".csv";
                System.out.println(dbf + " to " + outputCSV);
                DbfToCsv.convertDbfToCsv(dbf, outputCSV); // Конвертируем dbf в csv
                item.delete(); // Удаляем файл dbf
            }
        }
        System.out.println("Data was downloaded and converted");
    }

    //метод определения расширения файла
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        // если в имени файла есть точка и она не является первым символом в названии файла
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
            return fileName.substring(fileName.lastIndexOf(".")+1);
            // в противном случае возвращаем заглушку, то есть расширение не найдено
        else return "";
    }
}
