package com.example.interactivegift.translator.dictionary;

import java.util.Iterator;

public interface Dictionary<T> {

    String lookUp(String original);
    void insert(String original, String translation);
    Iterator<Pair<String>> iterator();

}
