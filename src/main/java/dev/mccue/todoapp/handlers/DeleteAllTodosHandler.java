package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeleteAllTodosHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public DeleteAllTodosHandler(SQLiteDataSource db) {
        super("DELETE", Pattern.compile("/"));
        this.db = db;
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     DELETE FROM todo
                     """)) {
            stmt.execute();
        }

        return new JsonResponse(Json.of(List.of()));
    }
}
