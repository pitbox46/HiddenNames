package github.pitbox46.hiddennames;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVObject {
    private static final Logger LOGGER = LogManager.getLogger();

    private int rows;
    private List<String> header;
    private Map<String,List<String>> table;

    public CSVObject(Map<String,List<String>> table) {
        this.header = new ArrayList<>(table.keySet());
        this.table = table;
        this.rows = table.values().stream().findAny().get().size();
    }

    public CSVObject(List<List<String>> table, List<String> header) {
        this(byRowToByColumn(table, header));
    }

    public List<String> getHeader() {
        return new ArrayList<>(header);
    }

    public Map<String,List<String>> getTable() {
        return new LinkedHashMap<>(table);
    }

    public int rows() {
        return rows;
    }

    public static List<List<String>> byColumnToByRow(Map<String,List<String>> table) {
        int rows = 0;
        List<String> flatList = new ArrayList<>();
        for(String string: table.keySet()) {
            rows = table.get(string).size();
            flatList.addAll(table.get(string));
        }
        List<List<String>> newTable = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            newTable.add(new ArrayList<>());
        }
        for(int i = 0; i < flatList.size(); i++) {
            newTable.get(i % rows).add(flatList.get(i));
        }
        return newTable;
    }

    public static Map<String,List<String>> byRowToByColumn(List<List<String>> table, List<String> header) {
        //Ensure column count is same for header and rows
        assert table.size() == 0 || table.get(0).size() == header.size();
        Map<String,List<String>> newTable = new LinkedHashMap<>();
        for(String column: header) {
            newTable.put(column, new ArrayList<>());
        }
        for(List<String> row: table) {
            for(int i = 0; i < row.size(); i++) {
                newTable.get(header.get(i)).add(row.get(i));
            }
        }
        return newTable;
    }

    public static CSVObject read(File file) {
        List<String> header = null;
        Map<String,List<String>> table = new LinkedHashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();
            if(line != null) {
                String[] splitLine = line.split("\",\"");
                for(int i = 0; i < splitLine.length; i++) {
                    // Gets rid of first quotation mark in row
                    if(i == 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(splitLine[i]);
                        builder.deleteCharAt(0);
                        table.put(builder.toString(), new ArrayList<>());
                    }
                    // Gets rid of final quotation mark in row
                    else if(i == splitLine.length - 1) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(splitLine[i]);
                        builder.deleteCharAt(builder.length() - 1);
                        table.put(builder.toString(), new ArrayList<>());
                    } else {
                        table.put(splitLine[i], new ArrayList<>());
                    }
                }
                header = new ArrayList<>(table.keySet());
                line = bufferedReader.readLine();
            }
            while(line != null) {
                int i = 0;
                for(String element: line.split("\",\"")) {
                    if(i >= table.size()) {
                        throw new IOException("Too many elements in CSV");
                    }
                    // Gets rid of first quotation mark in row
                    else if(i == 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(element);
                        builder.deleteCharAt(0);
                        table.get(header.get(i)).add(builder.toString());
                    }
                    // Gets rid of final quotation mark in row
                    else if(i == line.split("\",\"").length - 1) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(element);
                        builder.deleteCharAt(builder.length() - 1);
                        table.get(header.get(i)).add(builder.toString());
                    } else {
                        table.get(header.get(i)).add(element);
                    }
                    i++;
                }
                line = bufferedReader.readLine();
            }
        } catch(IOException e) {
            LOGGER.error("There was a problem while reading CSV from {}:\nMessage: {}", file.getPath(), e.getMessage());
        }
        return new CSVObject(table);
    }

    public static void write(File file, CSVObject csvObject) {
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(String.join("\",\"", csvObject.getHeader())).append("\"\n");
            for(List<String> row: byColumnToByRow(csvObject.table)) {
                builder.append("\"").append(String.join("\",\"", row)).append("\"\n");
            }
            builder.deleteCharAt(builder.length() - 1);
            writer.write(builder.toString());
        } catch (IOException e) {
            LOGGER.error("There was a problem while writing CSV to {}:\nMessage: {}", file.getPath(), e.getMessage());
        }
    }
}
