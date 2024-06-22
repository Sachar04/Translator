package com.example.interactivegift.translator.io;

import com.example.interactivegift.translator.dictionary.Dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class DictionaryFileReader implements AutoCloseable {
    private final Scanner scanner;

    public DictionaryFileReader(InputStream inputStream) {
        this.scanner = new Scanner(new BufferedReader(new InputStreamReader(inputStream)));
    }

    public void readInto(Dictionary<String> dictionary1) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                String[] parts = line.split(" -> ");
                if (parts.length == 2) {
                    dictionary1.insert(parts[0], parts[1]);
                }
            }
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
