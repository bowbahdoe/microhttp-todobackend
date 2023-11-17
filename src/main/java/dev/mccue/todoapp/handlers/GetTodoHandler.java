package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.IntoResponse;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import dev.mccue.todoapp.Utils;
import org.jspecify.annotations.Nullable;
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
    protected @Nullable IntoResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     SELECT id, title, completed, "order"
                     FROM todo
                     WHERE id = ?
                     """)) {
            stmt.setInt(1, Integer.parseInt(routeMatch.group("id")));
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new JsonResponse(Utils.todoJson(request, rs));
            }
            else {
                return new JsonResponse(Json.ofNull());
            }
        }
    }
}
