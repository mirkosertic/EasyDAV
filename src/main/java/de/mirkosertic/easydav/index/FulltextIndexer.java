package de.mirkosertic.easydav.index;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

import de.mirkosertic.easydav.event.Event;
import de.mirkosertic.easydav.event.EventListener;
import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.FileCreatedOrUpdatedEvent;
import de.mirkosertic.easydav.fs.FileDeletedEvent;
import de.mirkosertic.easydav.fs.FileFoundEvent;
import de.mirkosertic.easydav.fs.FileMovedEvent;

public class FulltextIndexer implements EventListener {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);
    private static final Version LUCENE_VERSION = Version.LUCENE_48;
    private static final int NUMBER_OF_FRAGMENTS = 5;

    private static enum UpdateCheckResult {
        UPDATED, UNMODIFIED
    }

    private static final int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
    private static final int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    private static final int keepAliveTime = 1000;
    private static final int workQueueSize = maxPoolSize;

    private ArrayBlockingQueue workQueue;
    private ThreadPoolExecutor executorPool;

    private final Analyzer analyzer;
    private final IndexWriter indexWriter;
    private final SearcherManager searcherManager;
    private final Thread commitThread;
    private final ContentExtractor contentExtractor;

    public FulltextIndexer(File aIndexDirectory, ContentExtractor aContentExtractor) throws IOException {
        analyzer = new StandardAnalyzer(LUCENE_VERSION);
        FSDirectory theIndexFSDirectory = FSDirectory.open(aIndexDirectory);
        if (theIndexFSDirectory.fileExists(IndexWriter.WRITE_LOCK_NAME)) {
            theIndexFSDirectory.clearLock(IndexWriter.WRITE_LOCK_NAME);
        }
        IndexWriterConfig theConfig = new IndexWriterConfig(LUCENE_VERSION, analyzer);
        indexWriter = new IndexWriter(theIndexFSDirectory, theConfig);
        searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());

        commitThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {

                    if (indexWriter.hasUncommittedChanges()) {
                        try {
                            indexWriter.commit();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // Do nothing here
                    }
                }
            }
        };

        commitThread.start();

        contentExtractor = aContentExtractor;

        workQueue = new ArrayBlockingQueue(workQueueSize);
        executorPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void handle(final Event aEvent) {
        if (aEvent instanceof FileFoundEvent) {
            executorPool.submit(new Runnable() {
                @Override
                public void run() {
                    FileFoundEvent theEvent = (FileFoundEvent) aEvent;
                    processFileFound(theEvent.getFile());
                }
            });
        }
        if (aEvent instanceof FileDeletedEvent) {
            executorPool.submit(new Runnable() {
                @Override
                public void run() {
                    FileDeletedEvent theEvent = (FileDeletedEvent) aEvent;
                    processFileDeleted(theEvent.getFile());
                }
            });
        }
        if (aEvent instanceof FileMovedEvent) {
            executorPool.submit(new Runnable() {
                @Override
                public void run() {
                    FileMovedEvent theEvent = (FileMovedEvent) aEvent;
                    processFileMoved(theEvent.getSource(), theEvent.getDestination());
                }
            });
        }
        if (aEvent instanceof FileCreatedOrUpdatedEvent) {
            executorPool.submit(new Runnable() {
                @Override
                public void run() {
                    FileCreatedOrUpdatedEvent theEvent = (FileCreatedOrUpdatedEvent) aEvent;
                    processFileCreatedOrUpdated(theEvent.getFile());
                }
            });
        }
    }

    private void processFileFound(FSFile aFile) {
        // This is called by the crawler
        if (!aFile.isDirectory()) {
            String theFileLocationID = toLocationID(aFile);
            try {
                UpdateCheckResult theResult = checkIfModified(theFileLocationID, aFile.lastModified());
                if (theResult == UpdateCheckResult.UPDATED) {
                    processFileCreatedOrUpdated(aFile);
                }
            } catch (IOException e) {
                LOGGER.error("Error checking file modification for {}", theFileLocationID, e);
            }
        }
    }

    private void processFileDeleted(FSFile aFile) {
        String theFileLocationID = toLocationID(aFile);
        try {
            if (aFile.isDirectory()) {
                indexWriter
                        .deleteDocuments(new WildcardQuery(new Term(IndexFields.FILENAME, theFileLocationID + "/*")));
                indexWriter.deleteDocuments(new TermQuery(new Term(IndexFields.FILENAME, theFileLocationID)));
            } else {
                indexWriter.deleteDocuments(new TermQuery(new Term(IndexFields.FILENAME, theFileLocationID)));
            }

            LOGGER.info("File {} removed from index", theFileLocationID);
        } catch (IOException e) {
            LOGGER.error("Error deleting file {} from index", theFileLocationID, e);
        }
    }

    private void processFileMoved(FSFile aSource, FSFile aDestination) {
        processFileDeleted(aSource);
        processFileCreatedOrUpdated(aDestination);
    }

    private void processFileCreatedOrUpdated(FSFile aFile) {
        // this is invoked during file manipulation
        if (contentExtractor.supportsFile(aFile)) {
            String theFileLocationID = toLocationID(aFile);

            processFileDeleted(aFile);
            Content theContent = contentExtractor.extractContentFrom(aFile);
            if (theContent != null) {
                try {
                    addToIndex(theFileLocationID, theContent);

                    LOGGER.info("File {} updated in index", theFileLocationID);
                } catch (IOException e) {
                    LOGGER.error("Error updating file at {}", theFileLocationID, e);
                }
            } else {
                LOGGER.warn("No content extracted for {}", theFileLocationID);
            }
        }
    }

    private String toLocationID(FSFile aFile) {
        if (aFile.parent() != null) {
            return toLocationID(aFile.parent()) + "/" + aFile.getName();
        }
        return aFile.getName();
    }

    private void addToIndex(String aLocationId, Content aContent) throws IOException {
        Document theDocument = new Document();

        theDocument.add(new StringField(IndexFields.FILENAME, aLocationId, Field.Store.YES));

        theDocument.add(new TextField(IndexFields.CONTENT, aContent.getFileContent(), Field.Store.YES));
        theDocument.add(new LongField(IndexFields.FILESIZE, aContent.getFileSize(), Field.Store.YES));
        theDocument.add(new StringField(IndexFields.LASTMODIFIED, "" + aContent.getLastModified(), Field.Store.YES));

        for (Map.Entry<String, String> theEntry : aContent.getMetadata().entrySet()) {
            theDocument.add(new StringField(IndexFields.META_PREFIX + theEntry.getKey(), theEntry.getValue(),
                    Field.Store.YES));
        }

        indexWriter.updateDocument(new Term(IndexFields.FILENAME, aLocationId), theDocument);
    }

    UpdateCheckResult checkIfModified(String aLocationId, long aLastModified) throws IOException {

        IndexSearcher theSearcher = searcherManager.acquire();
        try {
            Query theQuery = new TermQuery(new Term(IndexFields.FILENAME, aLocationId));
            TopDocs theDocs = theSearcher.search(theQuery, null, 100);
            if (theDocs.scoreDocs.length == 0) {
                return UpdateCheckResult.UPDATED;
            }
            if (theDocs.scoreDocs.length > 1) {
                // Multiple documents in index, we need to clean up
                return UpdateCheckResult.UPDATED;
            }
            ScoreDoc theFirstScore = theDocs.scoreDocs[0];
            Document theDocument = theSearcher.doc(theFirstScore.doc);

            long theStoredLastModified = Long.parseLong(theDocument.getField(IndexFields.LASTMODIFIED).stringValue());
            if (theStoredLastModified != aLastModified) {
                return UpdateCheckResult.UPDATED;
            }
            return UpdateCheckResult.UNMODIFIED;
        } finally {
            searcherManager.release(theSearcher);
        }
    }

    public QueryResult performQuery(String aSearchString) throws IOException {
        return performQuery(aSearchString, true, 100);
    }

    public QueryResult performQuery(String aQueryString, boolean aIncludeSimilarDocuments, int aMaxDocs) throws IOException {

        searcherManager.maybeRefreshBlocking();
        IndexSearcher theSearcher = searcherManager.acquire();

        List<QueryResultDocument> theResultDocuments = new ArrayList<>();

        long theStartTime = System.currentTimeMillis();

        DateFormat theDateFormat = new SimpleDateFormat("dd.MMMM.yyyy", Locale.ENGLISH);

        try {
            // Search only if a search query is given
            if (!StringUtils.isEmpty(aQueryString)) {

                QueryParser theParser = new QueryParser();

                Query theQuery = theParser.parse(aQueryString, IndexFields.CONTENT);

                MoreLikeThis theMoreLikeThis = new MoreLikeThis(theSearcher.getIndexReader());
                theMoreLikeThis.setAnalyzer(analyzer);
                theMoreLikeThis.setMinTermFreq(1);
                theMoreLikeThis.setMinDocFreq(1);
                theMoreLikeThis.setFieldNames(new String[]{IndexFields.CONTENT});

                TopDocs theDocs = theSearcher.search(theQuery, null, aMaxDocs);

                for (int i=0;i<theDocs.scoreDocs.length;i++) {
                    ScoreDoc theScoreDoc = theDocs.scoreDocs[i];
                    Document theDocument = theSearcher.doc(theScoreDoc.doc);

                    String theFoundFileName = theDocument.getField(IndexFields.FILENAME).stringValue();
                    Date theLastModified = new Date(Long.parseLong(theDocument.getField(IndexFields.LASTMODIFIED).stringValue()));

                    String theOriginalContent = theDocument.getField(IndexFields.CONTENT).stringValue();

                    StringBuilder theHighlightedResult = new StringBuilder(theDateFormat.format(theLastModified));
                    theHighlightedResult.append("&nbsp;-&nbsp;");
                    Highlighter theHighlighter = new Highlighter(new SimpleHTMLFormatter(), new QueryScorer(theQuery));
                    for (String theFragment : theHighlighter.getBestFragments(analyzer, IndexFields.CONTENT, theOriginalContent, NUMBER_OF_FRAGMENTS)) {
                        if (theHighlightedResult.length() > 0) {
                            theHighlightedResult = theHighlightedResult.append("...");
                        }
                        theHighlightedResult = theHighlightedResult.append(theFragment);
                    }

                    List<String> theSimilarFiles = new ArrayList<>();

                    if (aIncludeSimilarDocuments) {

                        Query theMoreLikeThisQuery = theMoreLikeThis.like(theDocs.scoreDocs[i].doc);
                        TopDocs theMoreLikeThisTopDocs = theSearcher.search(theMoreLikeThisQuery, 5);
                        for (ScoreDoc theMoreLikeThisScoreDoc : theMoreLikeThisTopDocs.scoreDocs) {
                            Document theMoreLikeThisDocument = theSearcher.doc(theMoreLikeThisScoreDoc.doc);

                            String theFilename = theMoreLikeThisDocument.getField(IndexFields.FILENAME).stringValue();
                            if (!theFoundFileName.equals(theFilename)) {
                                if (!theSimilarFiles.contains(theFilename)) {
                                    theSimilarFiles.add(theFilename);
                                }
                            }
                        }
                    }

                    theResultDocuments.add(new QueryResultDocument(theFoundFileName, theHighlightedResult.toString(), Long.parseLong(theDocument.getField(IndexFields.LASTMODIFIED).stringValue()), theSimilarFiles));
                }
            }

            return new QueryResult(System.currentTimeMillis() - theStartTime, theResultDocuments, theSearcher.getIndexReader().numDocs());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            searcherManager.release(theSearcher);
        }
    }
}
