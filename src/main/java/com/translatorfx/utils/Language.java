package com.translatorfx.utils;
import java.util.HashMap;

/**
 * <p>Define a Language with ist attributes.</p>.
 *
 * @author Francisco David Manzanedo Valle.
 * @version 1.0
 */
public class Language {

    /**The original Language to be translated.*/
    private final String from;
    /**The destination language.*/
    private final String to;
    /**Represent the original and the destination language sentences to be translated*/
    private final HashMap<String, String> sentences;

    /**
     * Creates a Language with its attributes .
     * @param from The original language to be translated.
     * @param to The destination language.
     */
    public Language(String from, String to){
        this.from = from;
        this.to = to;
        this.sentences = new HashMap<>();
    }

    /**
     * Gets the original language.
     * @return A String corresponding with the original language.
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Gets the destination Language
     * @return A String corresponding with the destination language.
     */
    public String getTo() {
        return this.to;
    }

    /**
     * Gets the sentences of the language.
     * @return A HashMap of Strings with the sentences in both languages.
     */
    public HashMap<String, String> getSentences() {
        return this.sentences;
    }

    /**
     * Gets the translation of a given language.
     * @param text String corresponding with the text to be translated.
     * @return String with the test translated.
     */
    public String getTranslation(String text){
        return this.sentences.get(text);
    }

    /**
     * Add a new sentence.
     * @param key String corresponding to the original language to be translated.
     * @param value String corresponding to the destination language.
     */
    public void addSentence(String key, String value){
        sentences.put(key, value);
    }

    /**
     * Overload of toString Method.
     * @return String with the original language to be translated and the
     * destination language.
     */
    @Override
    public String toString() {

        // for(Map.Entry<String, String> entry: sentences.entrySet()){
        //   result.append("; Key: ").append(entry.getKey()).append("; Value: ").append(entry.getValue());
       //}
       return "Language from: " + from + " to: " + to;
    }
}
