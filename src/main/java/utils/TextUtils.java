package utils;

import java.text.Normalizer;

public class TextUtils {

    /**
     * To checks if AI response matches the expected text based on keyword similarity.
     *
     * @param expected 
     * 		- Expected answer from JSON
     * @param actual   
     * 		- AI generated response
     * @param threshold 
     * 		- Similarity threshold (0.0 - 1.0)
     * @return boolean
     * 		- true if similarity >= threshold, false otherwise
     */
    public static boolean isResponseValid(String expected, String actual, double threshold) {
        if (expected == null || actual == null || expected.isEmpty() || actual.isEmpty()) {
            return false;
        }

        // Normalize text: lowercase, remove punctuation, normalize spaces
        expected = normalizeText(expected);
        actual = normalizeText(actual);

        String[] keywords = expected.split("\\s+");
        int matched = 0;

        for (String word : keywords) {
            if (!word.isBlank() && actual.contains(word)) {
                matched++;
            }
        }

        double similarity = (double) matched / keywords.length;
        Log.message("Keyword match similarity: " + similarity);

        return similarity >= threshold;
    }

    /**
     * To normalize the text(lowercase, remove punctuation, normalize Arabic diacritics)
     * 
     * @param text
     * @return String
     */
    private static String normalizeText(String text) {
        text = text.toLowerCase().trim();

        // Remove all punctuation
        text = text.replaceAll("[\\p{Punct}]", " ");

        // Normalize spaces
        text = text.replaceAll("\\s+", " ");

        // Remove Arabic diacritics (fatha, damma, kasra, etc.)
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                         .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return text;
    }
}
