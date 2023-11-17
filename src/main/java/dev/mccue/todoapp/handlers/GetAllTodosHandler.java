package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GetAllTodosHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public GetAllTodosHandler(SQLiteDataSource db) {
        super("GET", Pattern.compile("/"));
        this.db = db;
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     SELECT id, title, completed, "order"
                     FROM todo
                     """)) {
            var rs = stmt.executeQuery();
            var arrayBuilder = Json.arrayBuilder();
            while (rs.next()) {
                arrayBuilder.add(Json.objectBuilder()
                        .put("title", rs.getString("title"))
                        .put("completed", rs.getBoolean("completed"))
                        .put("url", "https://" + request.header("host") + "/" + rs.getInt("id"))
                        .put("order", rs.getInt("order")));
            }

            return new JsonResponse(arrayBuilder);
        }
    }
}
