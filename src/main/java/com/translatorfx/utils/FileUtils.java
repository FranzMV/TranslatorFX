package com.translatorfx.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>It's in charge of managing all the Input/Output corresponding to the writing and reading of text files.</p>
 * @author Francisco David Manzanedo Valle.
 * @version 1.0
 */
public class FileUtils {

    private final static String SEPARATOR =";";

    /**
     * Read a text files of language and return all its dictionaries.
     * @param path A path corresponding with the text file to read.
     * @return a List of {@link Language} with all its dictionaries.
     */
    public static List<Language> readLanguages(Path path){
        List<Language> languageList = new ArrayList<>();
        final String PARENT_PATH = path.getParent().toString()+"\\";
        try(Stream<String> stream = Files.lines(path)){
            languageList = stream.map(line -> {
               String[]parts = line.split(SEPARATOR);
               //Name of the languages
               Language language = new Language(parts[0],parts[1]);
               //Read the translation subfolder and files.
               try(BufferedReader r1 = new BufferedReader(new FileReader(PARENT_PATH + parts[2]));
                    BufferedReader r2 = new BufferedReader(new FileReader(PARENT_PATH + parts[3]))){
                   String l1, l2;
                   while ((l1 = r1.readLine()) != null && (l2 = r2.readLine()) !=null){
                       language.addSentence(l1.trim().toLowerCase().replaceAll("\\.$", ""),
                               l2.trim().toLowerCase().replaceAll("\\.$", ""));
                   }
               }catch (IOException ex){
                   MessageUtils.showError("Error", "An error occurred reading from translations folders");
               }
               return language;
            }).collect(Collectors.toList());

        }catch (IOException ex){
            MessageUtils.showError("Error", "An error occurred reading the file");
        }
        return languageList;
    }

    /**
     * Reads only 1000 bytes of a text file.
     * @param path A path to the text file.
     * @return A String corresponding with the first 1000 bytes of a file.
     * @throws IOException IOException
     */
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path)).substring(0,1000);
    }


    /**
     *Translates an original Language to another destination language.
     * @param filename The filename of the original language.
     * @param translationFile The filename of the destination language.
     * @param language A {@link Language} object to be translated.
     */
    public static void translateLanguageFile(String filename, String translationFile, Language language){
        try(PrintWriter pw = new PrintWriter(new FileWriter(translationFile))){
            Files.lines(Paths.get(filename))
                    .forEach(e->
                            pw.println(language.getTranslation(
                                    e.trim().toLowerCase().replaceAll("\\.$",""))));
        }catch (IOException e){
            MessageUtils.showError("Error","Error translating file");
        }
    }


    /**
     *Checks the languages of a given text files
     * @param filename The name of the file.
     * @param languageListAux A list of {@link Language}.
     * @return A List of {@link Language}.
     * @throws IOException IOException.
     */
    public static List<Language> checkLanguages(String filename, List<Language> languageListAux) throws IOException{
        Stream<String> stream = Files.lines(Paths.get(filename));
        Optional<String> firstLine = stream.findFirst().map(
                line -> line.trim().toLowerCase(Locale.ROOT).replaceAll("\\.$", ""));

        return languageListAux.stream()
                .filter(e-> e.getSentences().containsKey(firstLine.get().split("\\.$")[0]))
                .collect(Collectors.toList());
    }

    /**
     *Delete a directory if exists.
     * @param path A path of the directory.
     * @throws IOException IOException.
     */
    public static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.delete(path);
    }
}
