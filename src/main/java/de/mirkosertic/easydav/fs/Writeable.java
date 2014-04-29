package de.mirkosertic.easydav.fs;

import java.io.IOException;
import java.io.OutputStream;

public interface Writeable {

    OutputStream openWriteStream() throws IOException;

}
