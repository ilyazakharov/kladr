package ru.croc.kladr.dbf2csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfHeader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DbfToCsv {

    private static Function<Object, String> converterForDataType(char typecode, final Charset cs) {
        Function<Object, String> converter = null;
        switch (typecode) {
            case 'D':
                converter = date -> ((Date) date).toInstant().toString();
                break;
            case 'C':
                converter = cdata -> (new String((byte[]) cdata, cs)).trim();
                break;
            case 'F':
            case 'N':
                converter = String::valueOf;
                break;
            default:
                throw new IllegalArgumentException(
                        "Encountered an unknown typecode [" + typecode + "]");
        }
        return converter;
    }

    public static void convertDbfToCsv(String dbf, String csv) {
        Path dbfPath = Paths.get(dbf);
        Path outputPath = Paths.get(csv);
        final Charset inputCharset = Charset.forName("CP866");
        final Charset charset = Charset.forName("UTF-8");

        try (DbfReader reader = new DbfReader(dbfPath.toFile());
             CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(outputPath, charset),
                     CSVFormat.RFC4180.withDelimiter(';'))) {
            final DbfHeader dbfHeader = reader.getHeader();

            final int fieldCount = dbfHeader.getFieldsCount();
            final List<Function<Object, String>> fieldConverter = IntStream.range(0, fieldCount)
                    .map(i -> dbfHeader.getField(i).getDataType().byteValue)
                    .mapToObj(code -> converterForDataType((char) code, inputCharset))
                    .collect(Collectors.toList());

            final List<String> fieldNames = IntStream.range(0, fieldCount)
                    .mapToObj(i -> dbfHeader.getField(i).getName())
                    .collect(Collectors.toList());

            // Print CSV header
            csvPrinter.printRecord(fieldNames);

            // Print CSV data
            Object[] row = null;
            while ((row = reader.nextRecord()) != null) {
                for (int fieldIndex = 0; fieldIndex < fieldCount; ++fieldIndex) {
                    String fieldData = fieldConverter.get(fieldIndex).apply(row[fieldIndex]);
                    fieldData = fieldData.replace("\"", "");
                    fieldData = fieldData.replace(";", ",");
                    csvPrinter.print(fieldData);
                }
                csvPrinter.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
