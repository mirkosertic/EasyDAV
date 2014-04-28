package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;

class DefaultSavSessionProvider implements DavSessionProvider {

    @Override
    public boolean attachSession(WebdavRequest aRequest) throws DavException {
        DavSession theSession = aRequest.getDavSession();
        if (theSession == null) {
            theSession = new DefaultDavSession(aRequest);
            aRequest.setDavSession(theSession);
        }
        return true;
    }

    @Override
    public void releaseSession(WebdavRequest aRequest) {
        aRequest.setDavSession(null);
    }
}
