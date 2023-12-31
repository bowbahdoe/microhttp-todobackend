package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import dev.mccue.todoapp.TodoUrl;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GetTodoHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public GetTodoHandler(SQLiteDataSource db) {
        super("GET", Pattern.compile("/(?<id>.+)"));
        this.db = db;
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     SELECT id, title, completed, "order"
                     FROM todo
                     WHERE id = ?
                     """)) {
            stmt.setInt(1, Integer.parseInt(routeMatch.group("id")));
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new JsonResponse(
                        Json.objectBuilder()
                                .put("title", rs.getString("title"))
                                .put("completed", rs.getBoolean("completed"))
                                .put("url", new TodoUrl(request, rs.getInt("id")))
                                .put("order", rs.getInt("order"))
                );
            }
            else {
                return new JsonResponse(Json.ofNull());
            }
        }
    }
}
