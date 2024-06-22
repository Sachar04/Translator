package com.example.interactivegift.translator.dictionary;

import java.util.ArrayList;
import java.util.HashMap;

public interface StringDictionaryWithAdvancedLookUp extends Dictionary<String> {
     String lookUpIgnoringPunctuationMarks(String original);
    String translateSentence(String originalSentence);

}