package dev.mccue.todoapp.handlers;

import dev.mccue.microhttp.handler.IntoResponse;
import dev.mccue.microhttp.handler.RouteHandler;
import org.jspecify.annotations.Nullable;
import org.microhttp.Header;
import org.microhttp.Request;
import org.microhttp.Response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CorsHandler extends RouteHandler {
    public CorsHandler() {
        super("OPTIONS", Pattern.compile(".+"));
    }

    @Override
    protected IntoResponse handleRoute(Matcher routeMatch, Request request) {
        return () -> new Response(
                200, "OK", List.of(
                new Header("access-control-allow-headers", "accept, content-type"),
                new Header("access-control-allow-methods", "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH")
        ), new byte[] {});
    }
}
