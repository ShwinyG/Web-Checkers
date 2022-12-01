/*
Filename: GetSignInRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.appl.PlayerServices;
import spark.*;

import static spark.Spark.halt;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route used to get the Sign-in page and render it on the server.
 * It is used as a GET route in this program.
 */
public class GetSignInRoute implements Route{
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;

    // These are a bunch of attribute names and a couple String messages.
    static final String TITLE_ATTR = "title";
    static final String TITLE = "Web Checkers Sign in Page";
    static final String VIEW_NAME = "signin.ftl";

    /**
     * This is the Constructor for GetSignInRoute.
     * @param templateEngine the HTML template rendering engine.
     */
    public GetSignInRoute(final TemplateEngine templateEngine) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    }

    /**
     * Render the WebCheckers Sign-in page.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Sign-in page
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        final PlayerServices playerServices = httpSession.attribute(GetHomeRoute.PLAYERSERVICES_KEY);

        final Map<String, Object> vm = new HashMap<>();
        vm.put(TITLE_ATTR, TITLE);

        if (playerServices != null) {
            return templateEngine.render(new ModelAndView(vm, VIEW_NAME));  // classic return stmt, should be changed
        }
        else{
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }
    }
}
