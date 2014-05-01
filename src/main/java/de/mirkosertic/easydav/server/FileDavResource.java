package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.Deletable;
import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.FileMovedEvent;
import de.mirkosertic.easydav.fs.Renameable;
import de.mirkosertic.easydav.fs.UserID;
import de.mirkosertic.easydav.fs.Writeable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.webdav.*;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.*;
import org.apache.jackrabbit.webdav.property.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

class FileDavResource implements DavResource {

    private static final String LOCKTOKEN = "LOCKTOKEN";

    final FSFile file;
    final DavResourceFactory resourceFactory;
    final DavResourceLocator resourceLocator;
    final DavSession session;
    final ResourceFactory resFactory;
    final EventManager eventManager;

    private LockManager lockManager;
    private final DavPropertySet properties;

    FileDavResource(ResourceFactory aResFactory, FSFile aFile, DavSession aSession, DavResourceFactory aResourceFactory,
            DavResourceLocator aResourceLocator, EventManager aEventManager) {
        file = aFile;
        resourceFactory = aResourceFactory;
        resourceLocator = aResourceLocator;
        session = aSession;
        properties = new DavPropertySet();
        resFactory = aResFactory;
        eventManager = aEventManager;
    }

    void createNewEmptyCollection() {
        file.mkdirs();
    }

    OutputStream openStream() throws IOException, DavException {
        if (!(file instanceof Writeable)) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN);
        }
        Writeable theWriteable = (Writeable) file;
        return theWriteable.openWriteStream();
    }

    @Override
    public String getComplianceClass() {
        return DavCompliance.concatComplianceClasses(new String[] { DavCompliance._1_, DavCompliance._2_ });
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean isCollection() {
        return file.isDirectory();
    }

    @Override
    public String getDisplayName() {
        return file.getName();
    }

    @Override
    public DavResourceLocator getLocator() {
        return resourceLocator;
    }

    @Override
    public String getResourcePath() {
        if (resourceLocator.isRootLocation()) {
            return "/" + file.getName();
        }
        String theResourcePath = resourceLocator.getResourcePath();
        if (!theResourcePath.endsWith("/")) {
            return theResourcePath + "/" + file.getName();
        }
        return theResourcePath + '/' + file.getName();
    }

    @Override
    public String getHref() {
        String theHRef = resourceLocator.getHref(false);
        if (!theHRef.endsWith("/")) {
            return theHRef + "/" + file.getName();
        }
        return theHRef + file.getName();
    }

    @Override
    public long getModificationTime() {
        return file.lastModified();
    }

    @Override
    public DavPropertyName[] getPropertyNames() {
        return properties.getPropertyNames();
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName aPropertyName) {
        return properties.get(aPropertyName);
    }

    @Override
    public DavPropertySet getProperties() {
        return properties;
    }

    @Override
    public void setProperty(DavProperty<?> aProperty) {
        properties.add(aProperty);
    }

    @Override
    public void removeProperty(DavPropertyName aPropertyName) throws DavException {
        properties.remove(aPropertyName);
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> aChangeList) throws DavException {
        if (!exists()) {
            throw new DavException(DavServletResponse.SC_NOT_FOUND);
        }
        MultiStatusResponse theResponse = new MultiStatusResponse(getHref(), null);
        /*
         * loop over list of properties/names that were successfully altered
         * and them to the multistatus response respecting the result of the
         * complete action. in case of failure set the status to 'failed-dependency'
         * in order to indicate, that altering those names/properties would
         * have succeeded, if no other error occured.
         */
        for (PropEntry propEntry : aChangeList) {
            int statusCode = DavServletResponse.SC_OK;

            if (propEntry instanceof DavProperty) {
                theResponse.add(((DavProperty<?>) propEntry).getName(), statusCode);
            } else {
                theResponse.add((DavPropertyName) propEntry, statusCode);
            }
        }
        return theResponse;
    }

    protected UserID currentUserID() {
        String theUserID = ((DefaultDavSession)session).getCurrentUserID();
        if (StringUtils.isEmpty(theUserID)) {
            return UserID.ANONYMOUS;
        }
        return new UserID(theUserID);
    }

    @Override
    public void move(DavResource aDestination) throws DavException {
        if (!(file instanceof Renameable)) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN);
        }
        if (!exists()) {
            throw new DavException(DavServletResponse.SC_NOT_FOUND);
        }

        FileDavResource theFileResource = (FileDavResource) aDestination;
        Renameable theRenameable = (Renameable) file;

        if (!theRenameable.renameTo(theFileResource.file)) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN);
        }

        eventManager.fire(new FileMovedEvent(currentUserID(), file, theFileResource.file));
    }

    @Override
    public void copy(DavResource destination, boolean shallow) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public boolean isLockable(Type aLockType, Scope aScope) {
        return true;
    }

    @Override
    public boolean hasLock(Type aLockType, Scope aScope) {
        return lockManager.hasLock(LOCKTOKEN, this);
    }

    @Override
    public ActiveLock getLock(Type aLockType, Scope aScope) {
        return lockManager.getLock(aLockType, aScope, this);
    }

    @Override
    public ActiveLock[] getLocks() {
        ActiveLock theWriteLock = getLock(Type.WRITE, Scope.EXCLUSIVE);
        return (theWriteLock != null) ? new ActiveLock[] { theWriteLock } : new ActiveLock[0];
    }

    @Override
    public ActiveLock lock(LockInfo aLockInfo) throws DavException {
        return lockManager.createLock(aLockInfo, this);
    }

    @Override
    public ActiveLock refreshLock(LockInfo aLockInfo, String aLockToken) throws DavException {
        return lockManager.refreshLock(aLockInfo, aLockToken, this);
    }

    @Override
    public void unlock(String aLockToken) throws DavException {
        lockManager.releaseLock(aLockToken, this);
    }

    @Override
    public void addLockManager(LockManager aLockManager) {
        lockManager = aLockManager;
    }

    @Override
    public DavResourceFactory getFactory() {
        return resourceFactory;
    }

    @Override
    public DavSession getSession() {
        return session;
    }

    @Override
    public String getSupportedMethods() {
        StringBuilder theMethods = new StringBuilder("OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, COPY, LOCK, UNLOCK");
        if (file instanceof Writeable) {
            theMethods.append(", POST, PUT");
        }
        if (file instanceof Renameable) {
            theMethods.append(", MOVE");
        }
        if (file instanceof Deletable) {
            theMethods.append(", DELETE");
        }
        if (file.isDirectory()) {
            theMethods.append(", MKCOL");
        }
        return theMethods.toString();
    }

    @Override
    public void spool(OutputContext aOutputContext) throws IOException {
        aOutputContext.setContentLength(file.length());
        aOutputContext.setModificationTime(file.lastModified());
        // Just copy the file in case there is an output context...
        if (aOutputContext.hasStream()) {
            try (InputStream theFis = file.openInputStream()) {
                IOUtils.copyLarge(theFis, aOutputContext.getOutputStream());
            }
        }
    }

    @Override
    public DavResource getCollection() {
        return resFactory.createFileOrFolderResource(file.parent(), session, resourceFactory, resourceLocator);
    }

    @Override
    public void addMember(DavResource aDavResource, InputContext aInputContext) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public DavResourceIterator getMembers() {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void removeMember(DavResource aMember) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    void initProperties() {
        properties.add(new DefaultDavProperty<>(DavPropertyName.DISPLAYNAME, getDisplayName()));
        properties.add(new ResourceType(ResourceType.DEFAULT_RESOURCE));
        properties.add(new DefaultDavProperty<>(DavPropertyName.ISCOLLECTION, "0"));

        String theLastModified = IOUtil.getLastModified(getModificationTime());
        properties.add(new DefaultDavProperty<>(DavPropertyName.GETLASTMODIFIED, theLastModified));

        long theContentLength = file.length();
        if (theContentLength > IOUtil.UNDEFINED_LENGTH) {
            properties.add(new DefaultDavProperty<>(DavPropertyName.GETCONTENTLENGTH, theContentLength + ""));
        }
    }
}
