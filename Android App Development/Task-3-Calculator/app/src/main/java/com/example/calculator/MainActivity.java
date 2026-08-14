package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView display;
    private final StringBuilder expression = new StringBuilder();
    private boolean justEvaluated = false;
    private boolean errorState = false;
    private final DecimalFormat formatter = new DecimalFormat("0.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(this::onNumberClicked);
        }

        findViewById(R.id.btnDecimal).setOnClickListener(v -> onDecimalClicked());
        findViewById(R.id.btnPlus).setOnClickListener(v -> onOperatorClicked("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> onOperatorClicked("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorClicked("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorClicked("/"));
        findViewById(R.id.btnEquals).setOnClickListener(v -> evaluateExpression());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearCalculator());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> backspace());
    }

    private void onNumberClicked(View view) {
        if (errorState) clearCalculator();
        if (justEvaluated) {
            expression.setLength(0);
            justEvaluated = false;
        }
        expression.append(((Button) view).getText().toString());
        updateDisplay();
    }

    private void onDecimalClicked() {
        if (errorState) clearCalculator();
        if (justEvaluated) {
            expression.setLength(0);
            justEvaluated = false;
        }
        String current = getCurrentNumber();
        if (current.contains(".")) return;
        if (current.isEmpty()) expression.append("0");
        expression.append(".");
        updateDisplay();
    }

    private void onOperatorClicked(String operator) {
        if (errorState) clearCalculator();
        if (expression.length() == 0) {
            if (operator.equals("-")) {
                expression.append("-");
                updateDisplay();
            }
            return;
        }
        justEvaluated = false;
        char last = expression.charAt(expression.length() - 1);
        if (isOperator(last)) {
            expression.setCharAt(expression.length() - 1, operator.charAt(0));
        } else if (last == '.') {
            expression.append("0").append(operator);
        } else {
            expression.append(operator);
        }
        updateDisplay();
    }

    private void backspace() {
        if (errorState || justEvaluated) {
            clearCalculator();
            return;
        }
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            updateDisplay();
        }
    }

    private void clearCalculator() {
        expression.setLength(0);
        justEvaluated = false;
        errorState = false;
        display.setText("0");
    }

    private void updateDisplay() {
        display.setText(expression.length() == 0 ? "0" : expression.toString());
    }

    private String getCurrentNumber() {
        int i = expression.length() - 1;
        while (i >= 0 && !isOperator(expression.charAt(i))) i--;
        return expression.substring(i + 1);
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void evaluateExpression() {
        if (errorState || expression.length() == 0) return;

        String input = expression.toString();
        while (!input.isEmpty() && isOperator(input.charAt(input.length() - 1))) {
            input = input.substring(0, input.length() - 1);
        }
        if (input.isEmpty() || input.equals("-")) return;

        try {
            double result = calculate(input);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                showError();
                return;
            }
            String formatted = formatResult(result);
            expression.setLength(0);
            expression.append(formatted);
            display.setText(formatted);
            justEvaluated = true;
        } catch (RuntimeException e) {
            showError();
        }
    }

    private double calculate(String input) {
        if (input.startsWith("-")) return -calculate(input.substring(1));

        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        int start = 0;

        for (int i = 0; i < input.length(); i++) {
            if (isOperator(input.charAt(i))) {
                if (i == start) throw new IllegalArgumentException("Invalid expression");
                numbers.add(Double.parseDouble(input.substring(start, i)));
                operators.add(input.charAt(i));
                start = i + 1;
            }
        }

        if (start >= input.length()) throw new IllegalArgumentException("Missing number");
        numbers.add(Double.parseDouble(input.substring(start)));

        double current = numbers.get(0);
        List<Double> reducedNumbers = new ArrayList<>();
        List<Character> reducedOperators = new ArrayList<>();

        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            double next = numbers.get(i + 1);
            if (op == '*') {
                current *= next;
            } else if (op == '/') {
                if (Math.abs(next) < 1e-12) throw new ArithmeticException("Division by zero");
                current /= next;
            } else {
                reducedNumbers.add(current);
                reducedOperators.add(op);
                current = next;
            }
        }
        reducedNumbers.add(current);

        double result = reducedNumbers.get(0);
        for (int i = 0; i < reducedOperators.size(); i++) {
            if (reducedOperators.get(i) == '+') result += reducedNumbers.get(i + 1);
            else result -= reducedNumbers.get(i + 1);
        }
        return result;
    }

    private String formatResult(double value) {
        if (Math.abs(value) < 1e-12) value = 0.0;
        return formatter.format(value);
    }

    private void showError() {
        expression.setLength(0);
        display.setText("Error");
        errorState = true;
        justEvaluated = false;
    }
}
