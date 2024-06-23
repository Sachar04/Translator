package com.example.interactivegift.translator.io;

import com.example.interactivegift.translator.dictionary.Dictionary;
import com.example.interactivegift.translator.dictionary.Pair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class DictionaryFileWriter implements AutoCloseable {
    private final PrintWriter printWriter;

    public DictionaryFileWriter(File file) throws IOException {
        this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }

    public void write(Dictionary<String> dictionary1) {
        Iterator<Pair<String>> iterator = dictionary1.iterator();
        while(iterator.hasNext()) {
            Pair<String> pair = iterator.next();
            printWriter.println(pair.left() + " -> " + pair.right());
        }
    }

    public void clearFile(Dictionary<String> dictionary1) {
        Iterator<Pair<String>> iterator = dictionary1.iterator();
        while(iterator.hasNext()) {
            Pair<String> pair = iterator.next();
            printWriter.println("");
        }
    }

    @Override
    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
