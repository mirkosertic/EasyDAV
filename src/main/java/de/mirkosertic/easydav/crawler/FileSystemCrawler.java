package de.mirkosertic.easydav.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.FileFoundEvent;
import de.mirkosertic.easydav.fs.UserID;

public class FileSystemCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemCrawler.class);

    private final EventManager eventManager;

    public FileSystemCrawler(EventManager aEventManager) {
        eventManager = aEventManager;
    }

    public void crawl(UserID aUserID, FSFile aFile) {
        LOGGER.debug("Found file {}", aFile.getName());
        eventManager.fire(new FileFoundEvent(aUserID, aFile));
        if (aFile.isDirectory()) {
            for (FSFile theFile : aFile.listFiles()) {
                crawl(aUserID, theFile);
            }
        }
    }
}
