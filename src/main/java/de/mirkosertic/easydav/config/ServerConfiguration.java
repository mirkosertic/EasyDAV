package de.mirkosertic.easydav.config;

import org.aeonbits.owner.Config;

public interface ServerConfiguration extends Config {

    @DefaultValue("12000")
    @Key("webdavServerPort")
    int getWebDavServerPort();

    @DefaultValue("12001")
    @Key("webdavSearchUIPort")
    int getSearchUIServerPort();

    @Key("webdavIndexDataPath")
    String getIndexDataPath();
}
