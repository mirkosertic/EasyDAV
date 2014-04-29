package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.index.FulltextIndexer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchServlet extends HttpServlet {

    private FulltextIndexer indexer;

    public SearchServlet(FulltextIndexer aIndexer) {
        indexer = aIndexer;
    }

    @Override
    protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        aRequest.setAttribute("querystring","");
        aRequest.getRequestDispatcher("index.ftl").forward(aRequest, aResponse);
    }

    @Override
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        String theSearchString = aRequest.getParameter("querystring");
        if (!StringUtils.isEmpty(theSearchString)) {
            try {
                aRequest.setAttribute("queryResult", indexer.performQuery(theSearchString));
            } catch (Exception e) {
                e.printStackTrace();
            }
            aRequest.setAttribute("querystring", StringEscapeUtils.escapeHtml4(theSearchString));
        } else {
            aRequest.setAttribute("querystring","");
        }

        aRequest.getRequestDispatcher("index.ftl").forward(aRequest, aResponse);
    }
}