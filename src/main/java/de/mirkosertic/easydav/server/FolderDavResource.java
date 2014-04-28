package de.mirkosertic.easydav.server;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;

public class FolderDavResource extends FileDavResource {

    FolderDavResource(ResourceFactory aResFactory, File aFile, DavSession aSession,
            DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        super(aResFactory, aFile, aSession, aResourceFactory, aResourceLocator);
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        throw new NotImplementedException("Not implemented to add member");
    }

    @Override
    public DavResourceIterator getMembers() {

        final File[] members = file.listFiles();

        return new DavResourceIterator() {

            int position = 0;

            @Override
            public DavResource nextResource() {
                return next();
            }

            @Override
            public int size() {
                return members.length;
            }

            @Override
            public boolean hasNext() {
                return position < members.length;
            }

            @Override
            public DavResource next() {
                return toResource(members[position++]);
            }

            DavResource toResource(File aFile) {
                return resFactory.createFileOrFolderResource(aFile, session, resourceFactory, resourceLocator);
            }
        };
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        throw new NotImplementedException("Not implemented to remove member");
    }

    @Override
    public void spool(OutputContext aOutputContext) throws IOException {
        aOutputContext.setModificationTime(file.lastModified());
    }

    @Override
    void initProperties() {
        setProperty(new DefaultDavProperty<>(DavPropertyName.DISPLAYNAME, getDisplayName()));
        setProperty(new ResourceType(ResourceType.COLLECTION));
        setProperty(new DefaultDavProperty<>(DavPropertyName.ISCOLLECTION, "1"));
    }
}
