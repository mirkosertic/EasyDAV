package de.mirkosertic.easydav.fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface FSFile {

    boolean isDirectory();

    String getName();

    long lastModified();

    long length();

    void mkdirs();

    void delete() throws IOException;

    boolean exists();

    boolean renameTo(FSFile aNewFileName);

    OutputStream openWriteStream() throws FileNotFoundException;

    FileInputStream openInputStream() throws FileNotFoundException;

    List<FSFile> listFiles();

    FSFile asChild(String theResourcePath);

    FSFile parent();

    void setParent(FSFile aParent);
}
