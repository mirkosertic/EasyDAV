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

    private FulltextIndexer indexer;

    public SearchServlet(FulltextIndexer aIndexer) {
        indexer = aIndexer;
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

        UserID theUser;
        String theUserID = aRequest.getRemoteUser();
        if (StringUtils.isEmpty(theUserID)) {
            theUser = UserID.ANONYMOUS;
        } else {
            theUser = new UserID(theUserID);
        }

        String theSearchString = aRequest.getParameter("querystring");
        if (!StringUtils.isEmpty(theSearchString)) {
            try {
                aRequest.setAttribute("queryResult", indexer.performQuery(theUser, theSearchString));
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