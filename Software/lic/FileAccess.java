package isel.lic;

import java.io.*;
import java.util.ArrayList;

public class FileAccess {
    public static void main(String[] args) {
    }
    public static ArrayList<String> read(String fileName) {
        ArrayList<String> content = null;
        try (BufferedReader bR = new BufferedReader(new FileReader(fileName))) {
            content = getLines(bR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    private static ArrayList<String> getLines(BufferedReader bR) {
        ArrayList<String> content = null;
        try {
            String line;
            content = new ArrayList<>();
            while (null != (line = bR.readLine())) {
                content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    public static void write(String fileName, ArrayList<String> content) {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName))) {
            setLines(content, bW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void setLines(ArrayList<String> content, BufferedWriter bW) {
        try {
            for (String s : content) {
                bW.write(s);
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
