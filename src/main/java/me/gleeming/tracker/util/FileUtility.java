package me.gleeming.tracker.util;

import lombok.SneakyThrows;
import org.bson.Document;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtility {
    /**
     * Read JSON from document
     *
     * @param file File to read
     * @return Parsed JSON
     */
    @SneakyThrows
    public static Document readFromFile(File file) {
        StringBuilder s = new StringBuilder();

        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) s.append(myReader.nextLine());

        myReader.close();
        return Document.parse(s.toString());
    }

    /**
     * Write JSON to document
     *
     * @param file File to write to
     * @param document JSON
     */
    @SneakyThrows
    public static void write(File file, Document document) {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(document.toJson());
        fileWriter.close();
    }
}
