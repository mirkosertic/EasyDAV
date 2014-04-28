package de.mirkosertic.easydav.fs;

import java.io.*;
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

    OutputStream openWriteStream() throws IOException;

    InputStream openInputStream() throws IOException;

    List<FSFile> listFiles();

    FSFile asChild(String aResourcePath);

    FSFile parent();

    void setParent(FSFile aParent);
}
