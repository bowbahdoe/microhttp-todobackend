package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import dev.mccue.todoapp.TodoUrl;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatchTodoHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public PatchTodoHandler(SQLiteDataSource db) {
        super("PATCH", Pattern.compile("/(?<id>.+)"));
        this.db = db;
    }

    record PatchBody(
            Optional<String> title,
            Optional<Boolean> completed,
            Optional<Integer> order
    ) {
        static PatchBody fromJson(Json json) {
            return new PatchBody(
                    JsonDecoder.optionalField(json, "title", JsonDecoder::string),
                    JsonDecoder.optionalField(json, "completed", JsonDecoder::boolean_),
                    JsonDecoder.optionalField(json, "order", JsonDecoder::int_)
            );
        }
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        var id = Integer.parseInt(routeMatch.group("id"));
        var json = Json.readString(new String(request.body(), StandardCharsets.UTF_8));
        var body = PatchBody.fromJson(json);

        try (var conn = db.getConnection()) {
            conn.setAutoCommit(false);

            var title = body.title.orElse(null);
            if (title != null) {
                try (var stmt = conn.prepareStatement("""
                    UPDATE todo
                       SET title = ?
                    WHERE id = ?
                    """)) {
                    stmt.setString(1, title);
                    stmt.setInt(2, id);
                    stmt.execute();
                }
            }


            var completed = body.completed.orElse(null);
            if (completed != null) {
                try (var stmt = conn.prepareStatement("""
                    UPDATE todo
                       SET completed = ?
                    WHERE id = ?
                    """)) {
                    stmt.setBoolean(1, completed);
                    stmt.setInt(2, id);
                    stmt.execute();
                }
            }

            var order = body.order.orElse(null);
            if (order != null) {
                try (var stmt = conn.prepareStatement("""
                    UPDATE todo
                       SET "order" = ?
                    WHERE id = ?
                    """)) {
                    stmt.setInt(1, order);
                    stmt.setInt(2, id);
                    stmt.execute();
                }
            }

            conn.commit();

            try (var stmt = conn.prepareStatement("""
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
}
