package com.example.game.dragons.rules;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TextRule implements RuleComponent{
    private final String text;

    public TextRule(String filePath) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            this.text = content.toString();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error in this loading file: " + filePath, e);
        }
    }

    @Override
    public void showRule() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Rule:\n" + text);
            return;
        }
        JOptionPane.showMessageDialog(
                null,
                text,
                "Rules",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public String getText() {
        return text;
    }
}
