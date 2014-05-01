package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.DavSession;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

class DefaultDavSession implements DavSession {

    private final Set<Object> references;
    private final Set<String> lockTokens;
    private final HttpServletRequest servletRequest;

    DefaultDavSession(HttpServletRequest aServletRequest) {
        references = new HashSet<>();
        lockTokens = new HashSet<>();
        servletRequest = aServletRequest;
    }

    public String getCurrentUserID() {
        return servletRequest.getRemoteUser();
    }

    public boolean isPutRequest() {
        return "PUT".equals(servletRequest.getMethod());
    }

    @Override
    public void addReference(Object aReference) {
        references.add(aReference);
    }

    @Override
    public void removeReference(Object aReference) {
        references.remove(aReference);
    }

    @Override
    public void addLockToken(String aToken) {
        lockTokens.add(aToken);
    }

    @Override
    public String[] getLockTokens() {
        return lockTokens.toArray(new String[lockTokens.size()]);
    }

    @Override
    public void removeLockToken(String aToken) {
        lockTokens.remove(aToken);
    }
}
