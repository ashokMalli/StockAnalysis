package com.analysis.mutualFunds.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {

    private static final String filePath = "src/main/resources/invalidData.txt";

    public static void writeToFile(List<String> data) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))) {
               for(String schemeCode: data) {
                   writer.write(schemeCode);
                   writer.newLine();
               }
        }
    }

    public static List<String> readFromFile() throws IOException {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        }
        return data;
    }
}
