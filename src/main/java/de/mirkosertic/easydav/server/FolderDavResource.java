package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.FSFile;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FolderDavResource extends FileDavResource {

    FolderDavResource(ResourceFactory aResFactory, FSFile aFile, DavSession aSession,
            DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        super(aResFactory, aFile, aSession, aResourceFactory, aResourceLocator);
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void addMember(DavResource aResource, InputContext aInputContext) throws DavException {
        FileDavResource theFileResource = (FileDavResource) aResource;
        DefaultDavSession theDavSession = (DefaultDavSession) aResource.getSession();
        if (theDavSession.isPutRequest()) {
            // This also triggers file creation
            try (OutputStream theStream = theFileResource.openStream()) {
                if (aInputContext.hasStream()) {
                    // Normal file upload
                    IOUtils.copyLarge(aInputContext.getInputStream(), theStream);
                }
            } catch (Exception e) {
                throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } else {
            // MKCols Request
            try {
                theFileResource.createNewEmptyCollection();
            } catch (Exception e) {
                throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        }
    }

    @Override
    public DavResourceIterator getMembers() {

        final List<FSFile> members = file.listFiles();

        return new DavResourceIterator() {

            int position = 0;

            @Override
            public DavResource nextResource() {
                return next();
            }

            @Override
            public int size() {
                return members.size();
            }

            @Override
            public boolean hasNext() {
                return position < members.size();
            }

            @Override
            public DavResource next() {
                return toResource(members.get(position++));
            }

            DavResource toResource(FSFile aFile) {
                DavResourceLocator theChildLocator = resourceLocator.getFactory().createResourceLocator(resourceLocator.getPrefix(), resourceLocator.getHref(true)+ file.getName());
                return resFactory.createFileOrFolderResource(aFile, session, resourceFactory, theChildLocator);
            }
        };
    }

    @Override
    public void removeMember(DavResource aMember) throws DavException {
        if (!exists()) {
            throw new DavException(DavServletResponse.SC_NOT_FOUND);
        }

        FileDavResource theFileResource = (FileDavResource) aMember;
        try {
            theFileResource.file.delete();
        } catch (IOException e) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN, e);
        }
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

        String theLastModified = IOUtil.getLastModified(getModificationTime());
        setProperty(new DefaultDavProperty<>(DavPropertyName.GETLASTMODIFIED, theLastModified));
    }
}
