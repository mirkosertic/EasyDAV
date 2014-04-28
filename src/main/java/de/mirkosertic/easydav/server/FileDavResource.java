package de.mirkosertic.easydav.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ResourceType;

public class FileDavResource implements DavResource {

    protected File file;
    protected DavResourceFactory resourceFactory;
    protected DavResourceLocator resourceLocator;
    protected DavSession session;
    protected ResourceFactory resFactory;
    private DavPropertySet properties;

    FileDavResource(ResourceFactory aResFactory, File aFile, DavSession aSession, DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        file = aFile;
        resourceFactory = aResourceFactory;
        resourceLocator = aResourceLocator;
        session = aSession;
        properties = new DavPropertySet();
        resFactory = aResFactory;
    }

    @Override
    public String getComplianceClass() {
        return DavCompliance.concatComplianceClasses(new String[] { DavCompliance._1_, DavCompliance._2_});
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
        return file.toString();
    }

    @Override
    public DavResourceLocator getLocator() {
        return resourceLocator;
    }

    @Override
    public String getResourcePath() {
        if (resourceLocator.isRootLocation()) {
            return "/"+file.getName();
        }
        return resourceLocator.getResourcePath() + file.getName();
    }

    @Override
    public String getHref() {
        if (resourceLocator.isRootLocation()) {
            return "/"+file.getName();
        }
        return resourceLocator.getResourcePath() + file.getName();
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
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void move(DavResource aDestination) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void copy(DavResource destination, boolean shallow) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public boolean isLockable(Type aLockType, Scope aScope) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasLock(Type aLockType, Scope aScope) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ActiveLock getLock(Type aLockType, Scope aScope) {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ActiveLock[] getLocks() {
        return new ActiveLock[0];  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ActiveLock lock(LockInfo reqLockInfo) throws DavException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ActiveLock refreshLock(LockInfo reqLockInfo, String lockToken) throws DavException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlock(String aLockToken) throws DavException {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addLockManager(LockManager lockmgr) {
        // To change body of implemented methods use File | Settings | File Templates.
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
        return DavResource.METHODS;
    }

    @Override
    public void spool(OutputContext aOutputContext) throws IOException {
        aOutputContext.setContentLength(file.length());
        aOutputContext.setModificationTime(file.lastModified());
        // Just copy the file in case there is an output context...
        if (aOutputContext.hasStream()) {
            try(FileInputStream theFis = new FileInputStream(file)) {
                IOUtils.copyLarge(theFis, aOutputContext.getOutputStream());
            }
        }
    }

    @Override
    public DavResource getCollection() {
        return resFactory.createFileOrFolderResource(file.getParentFile(), session, resourceFactory, resourceLocator);
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public DavResourceIterator getMembers() {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        throw new NotImplementedException("Not implemented");
    }

    void initProperties() {
        properties.add(new DefaultDavProperty<>(DavPropertyName.DISPLAYNAME, getDisplayName()));
        properties.add(new ResourceType(ResourceType.DEFAULT_RESOURCE));
        properties.add(new DefaultDavProperty<>(DavPropertyName.ISCOLLECTION, "0"));
    }
}