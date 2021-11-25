package com.translatorfx.utils;

import java.nio.file.Path;

/**
 *<p>Represents a text file that store a {@link Language}. </p>
 * @author Francisco David Manzanedo Valle.
 * @version 1.0
 */
public class FileData {

    /**The name of the file*/
    private final String filename;
    /**The path of the file*/
    private final Path path;
    /**The time it takes for a thread to read a file*/
    private final long threadTotalTime;

    /**
     *Creates a file and its attributes.
     * @param filename The name of the file.
     * @param path The path of the file.
     * @param threadTotalTime The time it takes for a thread to read a file.
     */
    public FileData(String filename, Path path, long threadTotalTime){
        this.filename = filename;
        this.path = path;
        this.threadTotalTime = threadTotalTime;
    }

    /**
     *Gets the filena.
     * @return String corresponding to the filename.
     */
    public String getFilename() { return filename; }

    /**
     *Gets the path of the file.
     * @return A Path with the path of the file.
     */
    public Path getPath() { return path; }

    /**
     *Gets the time it takes for a thread to read a file.
     * @return A long with the representing the time it takes for a thread to read a file.
     */
    public long getThreadTotalTime(){ return threadTotalTime; }

    /**
     *Overload o toString method.
     * @return A String representing the name of the file.
     */
    @Override
    public String toString() { return filename ;  }
}
