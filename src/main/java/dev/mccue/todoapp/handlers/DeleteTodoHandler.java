package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeleteTodoHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public DeleteTodoHandler(SQLiteDataSource db) {
        super("DELETE", Pattern.compile("/(?<id>.+)"));
        this.db = db;
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        var id = Integer.parseInt(routeMatch.group("id"));
        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     DELETE FROM todo
                     WHERE id = ?
                     """)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
        return new JsonResponse(Json.ofNull());
    }
}
