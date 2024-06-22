package com.example.interactivegift.translator;
import com.example.interactivegift.R;
import com.example.interactivegift.translator.dictionary.Pair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.interactivegift.translator.dictionary.StringDictionary;
import com.example.interactivegift.translator.io.DictionaryFileReader;
import com.example.interactivegift.translator.io.DictionaryFileWriter;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputText;
    private Button translateButton;
    private TextView resultText;
    private Button loadFileButton, saveFileButton, printDictionaryButton, addEntryButton, lookUpWordButton;
    private static final StringDictionary dictionary1 = new StringDictionary();
    private static final File dictionaryFile = new File("res/dict.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.interactivegift.R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        translateButton = findViewById(R.id.translateButton);
        resultText = findViewById(R.id.resultText);
        loadFileButton = findViewById(R.id.loadFileButton);
        saveFileButton = findViewById(R.id.saveFileButton);
        printDictionaryButton = findViewById(R.id.printDictionaryButton);
        addEntryButton = findViewById(R.id.addEntryButton);
        lookUpWordButton = findViewById(R.id.lookUpWordButton);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                String translated = translate(input);
                resultText.setText(translated);
            }
        });

        loadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntriesFromFile();
            }
        });

        saveFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntriesToFile();
            }
        });

        printDictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printDictionary();
            }
        });

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEntryDialog();
                //addDictionaryEntry();
            }
        });

        lookUpWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String original = inputText.getText().toString();
                String translation = lookUpWord(original);
                resultText.setText(translation);
            }
        });
    }

    private String translate(String text) {
        return dictionary1.translateSentence(text);
    }

    private void saveEntriesToFile() {
        try (DictionaryFileWriter writer = new DictionaryFileWriter(dictionaryFile)) {
            writer.write(dictionary1);
        } catch (IOException e) {
            resultText.setText("Error writing to file: " + e.getMessage());
        }
    }

    private void addEntriesFromFile() {
        InputStream inputStream = getResources().openRawResource(R.raw.dict_en_de);

        try (DictionaryFileReader reader = new DictionaryFileReader(inputStream)) {
            reader.readInto(dictionary1);
        }

    }


    private String lookUpWord(String original) {
        String translation = dictionary1.lookUp(original);
        return translation != null ? translation : "No translation found for: " + original;
    }

    private void printDictionary() {
        StringBuilder sb = new StringBuilder("Dictionary entries:\n");
        for (Pair<String> pair : dictionary1) {
            sb.append(pair.left()).append(" -> ").append(pair.right()).append("\n");
        }
        resultText.setText(sb.toString());
    }

    private void showAddEntryDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_add_entry, null);

        // Find views in the dialog layout
        final TextInputEditText translationEditText = dialogView.findViewById(R.id.translationEditText);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView)
                .setTitle("Add Dictionary Entry")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String original = inputText.getText().toString().trim();
                        String translation = translationEditText.getText().toString().trim();

                        if (!original.isEmpty() && !translation.isEmpty()) {
                            dictionary1.insert(original, translation);
                            // Optionally, clear the input fields after inserting
                            inputText.getText().clear();
                            translationEditText.getText().clear();
                        } else {
                            // Handle case where either original or translation is empty
                            // You can show a toast or message to the user
                            Toast.makeText(MainActivity.this, "Please enter both original word and translation.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    private void addDictionaryEntry() {
        String original = inputText.getText().toString();
        String translation = inputText.getText().toString(); // make as a separate function add translation
        dictionary1.insert(original, translation);
    }
}
