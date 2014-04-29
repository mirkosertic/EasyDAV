package de.mirkosertic.easydav.index;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mirkosertic.easydav.fs.FSFile;

public class ContentExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);    

    private final Set<String> supportedExtensions;
    private final Tika tika;

    public ContentExtractor() {
        supportedExtensions = new HashSet<>();
        supportedExtensions.add("txt");
        supportedExtensions.add("msg");
        supportedExtensions.add("pdf");
        supportedExtensions.add("doc");
        supportedExtensions.add("docx");
        supportedExtensions.add("ppt");
        supportedExtensions.add("pptx");
        supportedExtensions.add("rtf");
        supportedExtensions.add("html");

        tika = new Tika();
    }

    public Content extractContentFrom(FSFile aFile) {
        try {
            Metadata theMetaData = new Metadata();

            String theStringData;
            // Files under 10 Meg are read into memory as a whole
            if (aFile.length() < 1024 * 1024 * 4) {
                try (InputStream theStream = aFile.openInputStream()) {
                    byte[] theData = IOUtils.toByteArray(theStream);
                    theStringData = tika.parseToString(new ByteArrayInputStream(theData), theMetaData);
                }
            } else {
                try (InputStream theStream = aFile.openInputStream()) {
                    theStringData = tika.parseToString(new BufferedInputStream(theStream), theMetaData);
                }
            }

            Content theContent = new Content(theStringData, aFile.length(), aFile.lastModified());
            for (String theName : theMetaData.names()) {
                theContent.addMetaData(theName.toLowerCase(), theMetaData.get(theName));
            }

            String theFileName = aFile.toString();
            int p = theFileName.lastIndexOf(".");
            if (p > 0) {
                String theExtension = theFileName.substring(p + 1);
                theContent.addMetaData(IndexFields.EXTENSION, theExtension);
            }

            return theContent;
            
        } catch (Exception e) {
            LOGGER.error("Error extracting content from {}", aFile.getName(), e);
        }

        return null;
    }

    public boolean supportsFile(FSFile aFile) {
        if (aFile.isDirectory()) {
            return false;
        }
        String theFilename = aFile.getName();
        int p = theFilename.lastIndexOf(".");
        if (p < 0) {
            return false;
        }
        String theExtension = theFilename.substring(p + 1);
        return supportedExtensions.contains(theExtension.toLowerCase());
    }
}
