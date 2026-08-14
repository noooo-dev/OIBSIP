package com.example.unitconverter;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Spinner sourceSpinner;
    private Spinner targetSpinner;
    private EditText inputValue;
    private TextView resultText;

    private final String[] categories = {"Length", "Weight", "Volume", "Temperature"};

    private final String[] lengthUnits = {
            "Centimetres (cm)", "Metres (m)", "Kilometres (km)",
            "Inches (in)", "Feet (ft)", "Miles (mi)"
    };

    private final String[] weightUnits = {
            "Grams (g)", "Kilograms (kg)", "Pounds (lb)", "Ounces (oz)"
    };

    private final String[] volumeUnits = {
            "Millilitres (ml)", "Litres (L)", "Cups", "Gallons (gal)"
    };

    private final String[] temperatureUnits = {
            "Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = findViewById(R.id.categorySpinner);
        sourceSpinner = findViewById(R.id.sourceSpinner);
        targetSpinner = findViewById(R.id.targetSpinner);
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);

        inputValue.setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
        );

        setupCategorySpinner();

        findViewById(R.id.convertButton).setOnClickListener(v -> convertValue());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                updateUnitSpinners(position);
                inputValue.setText("");
                resultText.setText(R.string.result_placeholder);
            }
        });
    }

    private void updateUnitSpinners(int categoryPosition) {
        String[] units;

        switch (categoryPosition) {
            case 1:
                units = weightUnits;
                break;
            case 2:
                units = volumeUnits;
                break;
            case 3:
                units = temperatureUnits;
                break;
            case 0:
            default:
                units = lengthUnits;
                break;
        }

        setSpinnerAdapter(sourceSpinner, units);
        setSpinnerAdapter(targetSpinner, units);

        if (units.length > 1) {
            targetSpinner.setSelection(1);
        }
    }

    private void setSpinnerAdapter(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void convertValue() {
        String input = inputValue.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value.", Toast.LENGTH_SHORT).show();
            return;
        }

        final double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        int category = categorySpinner.getSelectedItemPosition();
        int source = sourceSpinner.getSelectedItemPosition();
        int target = targetSpinner.getSelectedItemPosition();

        double converted;

        try {
            switch (category) {
                case 0:
                    converted = convertLength(value, source, target);
                    break;
                case 1:
                    converted = convertWeight(value, source, target);
                    break;
                case 2:
                    converted = convertVolume(value, source, target);
                    break;
                case 3:
                    converted = convertTemperature(value, source, target);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown category");
            }

            String sourceUnit = sourceSpinner.getSelectedItem().toString();
            String targetUnit = targetSpinner.getSelectedItem().toString();

            resultText.setText(String.format(
                    Locale.getDefault(),
                    "%.4f %s = %.4f %s",
                    value, sourceUnit, converted, targetUnit
            ));

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Unable to convert these units.", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertLength(double value, int source, int target) {
        double[] toMetres = {
                0.01,       // cm
                1.0,        // m
                1000.0,     // km
                0.0254,     // in
                0.3048,     // ft
                1609.344    // mi
        };
        return value * toMetres[source] / toMetres[target];
    }

    private double convertWeight(double value, int source, int target) {
        double[] toGrams = {
                1.0,        // g
                1000.0,     // kg
                453.59237,  // lb
                28.349523125 // oz
        };
        return value * toGrams[source] / toGrams[target];
    }

    private double convertVolume(double value, int source, int target) {
        double[] toLitres = {
                0.001,      // ml
                1.0,        // L
                0.2365882365, // US cup
                3.785411784 // US gallon
        };
        return value * toLitres[source] / toLitres[target];
    }

    private double convertTemperature(double value, int source, int target) {
        double celsius;

        switch (source) {
            case 0:
                celsius = value;
                break;
            case 1:
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case 2:
                celsius = value - 273.15;
                break;
            default:
                throw new IllegalArgumentException("Invalid source temperature");
        }

        switch (target) {
            case 0:
                return celsius;
            case 1:
                return celsius * 9.0 / 5.0 + 32.0;
            case 2:
                return celsius + 273.15;
            default:
                throw new IllegalArgumentException("Invalid target temperature");
        }
    }

    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                    int position, long id) {
            onItemSelected(position);
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
        }

        public void onItemSelected(int position) {
        }
    }
}
