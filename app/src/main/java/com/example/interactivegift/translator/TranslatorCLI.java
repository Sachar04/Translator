package com.example.interactivegift.translator;


import android.content.res.Resources;

import com.example.interactivegift.R;
import com.example.interactivegift.translator.dictionary.Pair;
import com.example.interactivegift.translator.dictionary.StringDictionary;
import com.example.interactivegift.translator.io.DictionaryFileReader;
import com.example.interactivegift.translator.io.DictionaryFileWriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;

public class TranslatorCLI {
    private final PrintStream consoleOut;
    private final Scanner consoleIn;
    private static final StringDictionary dictionary1 = new StringDictionary();
    private static final StringDictionary sentence = new StringDictionary();
//    private final StringDictionaryWithAdvancedLookUp dictionary;
    private static final File dictionaryFile = new File("app/res/dict-en-de.txt");



    public TranslatorCLI(PrintStream consoleOut, Scanner consoleIn) {
            this.consoleOut = consoleOut;
            this.consoleIn = consoleIn;
            consoleIn.useDelimiter("\n");
//            this.dictionary = new StringBinTreeDictionary();
         }

         public static void main(String[] args) throws FileNotFoundException {
            new TranslatorCLI(System.out, new Scanner(System.in)).run();
         }

         public void run() throws FileNotFoundException {
            boolean running = true;
            while (running) {
                running = showMainMenu();
             }
         }

         public boolean showMainMenu() {
            consoleOut.println("\n(1) Print current dictionary");
            consoleOut.println("(2) Add dictionary entry manually");
            consoleOut.println("(3) Add entries from dictionary file");
            consoleOut.println("(4) Save entries to dictionary file");
            consoleOut.println("(5) Look up a word");
            consoleOut.println("(6) Translate a sentence");
            consoleOut.println("(9) Exit");

            if (consoleIn.hasNextInt()) {
                switch (consoleIn.nextInt()) {
                    case 1: printDictionary(); break;
                    case 2: addDictionaryEntry(dictionary1); break;
                    case 3: addEntriesFromFile(); break;
                    case 4: saveEntriesToFile(); break;
                    case 5:
                        String original = consoleIn.next();
                        String translation = lookUpWord(original);
                        System.out.println(translation);break;
                    case 6: translateTheSentence(dictionary1); break;
                    case 9: return false;
                    default: showMainMenu();
                }
            } else {
                consoleIn.next();
                showMainMenu();
            }
         return true;
        }

    private void saveEntriesToFile() {
        try (DictionaryFileWriter writer = new DictionaryFileWriter(dictionaryFile)) {
            writer.write(dictionary1);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void addEntriesFromFile() {
        InputStream inputStream = getResources().openRawResource(R.raw.dict_en_de);

        try (DictionaryFileReader reader = new DictionaryFileReader(inputStream)) {
            reader.readInto(dictionary1);
        }
    }

    private Resources getResources() {
        return null;
    }


    private static String lookUpWord(String original) {
            String translation = dictionary1.lookUp(original);
            if (translation != null) {
                return translation;
            } else {
                return "No translation found for: " + original;
            }
        }

    private void printDictionary() {
//        System.out.println(dictionary1);  - explained in toString() Override see StringDictionary.java line 32
        System.out.println("Dictionary entries:\n");
        Iterator<Pair<String>> iterator = dictionary1.iterator();
        while (iterator.hasNext()) {
            Pair<String> pair = iterator.next();
            System.out.println(pair.left() + " -> " + pair.right()); //iterates through the whole dictionary and returns saved pairs separated with "->"
        }
    }

    private void addDictionaryEntry(StringDictionary dictionary1) {

            consoleOut.println("Enter the original word:\n");
            String original = consoleIn.next();
            consoleOut.println("Enter the translation:\n");
            String translation = consoleIn.next();
            dictionary1.insert(original, translation); //inserts a word and its translation into the record and dictionary
    }

    public void translateTheSentence(StringDictionary dictionary1){
        System.out.println("Please write a sentence you want to translate:\n");
        String originalSentence = consoleIn.next();
        System.out.println(dictionary1.translateSentence(originalSentence)); //returns a Sentence translated word-by-word (with adequate punctuation if specified)
    }

}
