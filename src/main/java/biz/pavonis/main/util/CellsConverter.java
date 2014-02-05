package biz.pavonis.main.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

public class CellsConverter {

    public static void main(String[] args) {
        CellsConverter cc = new CellsConverter();
        cc.convertCells();

    }

    private void convertCells() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("old_patterns");
        try {
            File oldPatternsFolder = new File(url.toURI());
            GsonBuilder builder = new GsonBuilder();
            for (File file : oldPatternsFolder.listFiles()) {
                List<List<Boolean>> pattern = fetchPattern(file);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src/main/resources/patterns/"
                        + file.getName().replace(".cells", ".json"))))) {
                    writer.write(builder.create().toJson(pattern));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private List<List<Boolean>> fetchPattern(File file) {
        List<List<Boolean>> pattern = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("!") || line.isEmpty()) {
                    continue;
                }
                List<Boolean> patternRow = new ArrayList<>();
                for (int i = 0; i < line.length(); i++) {
                    patternRow.add(line.charAt(i) == '.' ? false : true);
                }
                pattern.add(patternRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pattern;
    }
}
