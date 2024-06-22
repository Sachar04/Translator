package com.example.interactivegift.translator.dictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import static java.lang.Character.isAlphabetic;

public class StringDictionary implements Dictionary<String>, StringDictionaryWithAdvancedLookUp, Iterable<Pair<String>>{
    private final List<Pair<String>> pairs = new ArrayList<>(); // Creates a new entry and saves it to an arrayList
    @Override
    public String lookUp(String original) {
        for (Pair pair : pairs) {
            if (pair.left().equals(original)) {
                return pair.right().toString();
            }
        }
        return null;
    }

    @Override
    public void insert(String original, String translation) {
        Pair<String> pair = new Pair<>(original, translation);
        pairs.add(pair);
    }

    @Override
    // define toString to allow pair.right().toString() see line 14 of this file
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair pair : pairs) {
            sb.append("").append(pair.left()).append(" -> ").append(pair.right()).append("\n");
        } // alternative to Iterator method in Dictionary (I found out while watching a tutorial, overrode toString because lookUp wouldn't accept it than and then thought that it would be a nice idea to implement the iteration method right inside :)
        return sb.toString();
    }

    @Override
    public Iterator<Pair<String>> iterator() {
        return pairs.iterator();
    }

    @Override
    public String lookUpIgnoringPunctuationMarks(String original) {
            String originalWithoutPunctuation = cutPunctuation(original);
            String translation = lookUp(originalWithoutPunctuation);
            if(translation != null){
                return addPunctuationBack(original, translation);
            }
            return translation;
    }

    // identifies whether an entered word has punctuation behind it
        public static String cutPunctuation(String original){
//            if (original == null || original.isEmpty()) {
//                return original;
//            }
            int len = original.length();
            char lastChar = original.charAt(len - 1);
            if (isPunctuation(lastChar)) {
                return original.substring(0, len - 1); //if the word has punctuation, returns it without
            }
            return original; //else
        }

        public static boolean isPunctuation ( char c){
            if(c == '.' || c == ',' || c == ';' || c == ':' || c == '!' || c == '?'){ //specifies what is identified as punctuation that will be cut or added
                return true;
            }else{return false;}
        }

        public static String addPunctuationBack(String original, String translation){
            int len = original.length();
            char lastChar = original.charAt(len -1);
            //checks whether the word had punctuation
            if(isPunctuation(lastChar)){
                String translationWithPunctuation = translation + lastChar; //adds punctuation back
                return translationWithPunctuation;
            }
            return translation; //adds no punctuation
        }

    @Override
    // accepts a Sentence to translate, returns word by word translated sentence
    public String translateSentence(String originalSentence) {
        String originalArray[] = originalSentence.toString().split(" "); //splits by space
        ArrayList<String> translatedArray = new ArrayList<>(); //creates a dynamic array
        int len = originalArray.length;
        for (int i = 0; i < len; i++) {
            translatedArray.add(i, lookUpIgnoringPunctuationMarks(originalArray[i])); //iterates word by word, gives a word into lookUpPunctuation.. and stores the returned
        }
        return String.join(" ", translatedArray); //converts from ArrayList format into a String format
    }
}
