package com.example.interactivegift.translator;
import com.example.interactivegift.R;
import com.example.interactivegift.translator.dictionary.Pair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private static final File emptyFile = new File("res/raw/clear.txt");

    private int selectedButtonId = -1;

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

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dialogView = inflater.inflate(R.layout.choose_language, null);

        // Find views in the dialog layout
        final ImageButton en_de = dialogView.findViewById(R.id.en_de);
        final ImageButton de_en = dialogView.findViewById(R.id.de_en);
        final ImageButton de_ru = dialogView.findViewById(R.id.de_ru);
        final ImageButton ru_de = dialogView.findViewById(R.id.ru_de);
        final Button confirmButton = new Button(this);
        confirmButton.setText("Confirm");

        // Highlight logic
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonHighlights(dialogView);
                v.setSelected(true);  // Highlight the clicked button
                selectedButtonId = v.getId();  // Save the selected button ID
                applyBlurredBackground(dialogView);  // Apply blurred background
            }
        };

        en_de.setOnClickListener(buttonClickListener);
        de_en.setOnClickListener(buttonClickListener);
        de_ru.setOnClickListener(buttonClickListener);
        ru_de.setOnClickListener(buttonClickListener);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme);
        builder.setView(dialogView)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if (selectedButtonId != -1) {
                        handleButtonSelection(selectedButtonId);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void resetButtonHighlights(View dialogView) {
        ImageButton en_de = dialogView.findViewById(R.id.en_de);
        ImageButton de_en = dialogView.findViewById(R.id.de_en);
        ImageButton de_ru = dialogView.findViewById(R.id.de_ru);
        ImageButton ru_de = dialogView.findViewById(R.id.ru_de);

        en_de.setSelected(false);
        de_en.setSelected(false);
        de_ru.setSelected(false);
        ru_de.setSelected(false);
    }

    private void handleButtonSelection(int buttonId) {
        int fileNameResId;
        if (buttonId == R.id.en_de) {
            fileNameResId = R.raw.dict_en_de;
        } else if (buttonId == R.id.de_en) {
            fileNameResId = R.raw.dict_de_en;
        } else if (buttonId == R.id.de_ru) {
            fileNameResId = R.raw.dict_de_ru;
        } else if (buttonId == R.id.ru_de) {
            fileNameResId = R.raw.dict_ru_de;
        } else throw new IllegalStateException("Unexpected value: " + buttonId);

        if (fileNameResId != -1) {
            InputStream inputStream = getResources().openRawResource(fileNameResId);
            try (DictionaryFileReader reader = new DictionaryFileReader(inputStream)) {
                reader.readInto(dictionary1);
            }
        }
    }

    private void applyBlurredBackground(View dialogView) {
        ConstraintLayout layout = dialogView.findViewById(R.id.dialogId);
        layout.setBackgroundColor(Color.parseColor("#33AAAAAA"));  // 20% blurred grey
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
