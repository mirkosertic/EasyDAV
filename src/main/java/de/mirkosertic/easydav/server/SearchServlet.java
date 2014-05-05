package de.mirkosertic.easydav.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import de.mirkosertic.easydav.fs.UserID;
import de.mirkosertic.easydav.index.FulltextIndexer;

public class SearchServlet extends HttpServlet {

    private final FulltextIndexer indexer;
    private final ConfigurationManager configurationManager;

    public SearchServlet(FulltextIndexer aIndexer, ConfigurationManager aConfigurationManager) {
        indexer = aIndexer;
        configurationManager = aConfigurationManager;
    }

    @Override
    protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException,
            IOException {
        aRequest.setAttribute("querystring", "");
        aRequest.getRequestDispatcher("index.ftl").forward(aRequest, aResponse);
    }

    @Override
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException,
            IOException {

        Configuration theConfiguration = configurationManager.getConfigurationFor(aRequest);

        String theSearchString = aRequest.getParameter("querystring");
        if (!StringUtils.isEmpty(theSearchString)) {
            try {
                aRequest.setAttribute("queryResult", indexer.performQuery(theConfiguration.getRootFolder(), theSearchString));
            } catch (Exception e) {
                e.printStackTrace();
            }
            aRequest.setAttribute("querystring", StringEscapeUtils.escapeHtml4(theSearchString));
        } else {
            aRequest.setAttribute("querystring", "");
        }

        aRequest.getRequestDispatcher("index.ftl").forward(aRequest, aResponse);
    }
}