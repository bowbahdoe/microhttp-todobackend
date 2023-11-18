package dev.mccue.todoapp;

import dev.mccue.json.Json;
import dev.mccue.microhttp.handler.Handler;
import dev.mccue.microhttp.json.JsonResponse;
import dev.mccue.todoapp.handlers.*;
import org.microhttp.EventLoop;
import org.microhttp.NoopLogger;
import org.microhttp.OptionsBuilder;
import org.sqlite.SQLiteDataSource;

import java.util.List;

public final class Main {
    static SQLiteDataSource db() throws Exception {
        var db = new SQLiteDataSource();
        db.setUrl("jdbc:sqlite:todos.db");

        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS todo(
                        id INTEGER NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        completed BOOLEAN NOT NULL DEFAULT false,
                        "order" INTEGER NOT NULL DEFAULT 0
                     )
                     """)) {
            stmt.execute();
        }

        return db;
    }
    public static void main(String[] args) throws Exception {
        var notFound = new JsonResponse(404, Json.of("Not Found"));
        var genericError = new JsonResponse(500, Json.of("Internal Server Error"));

        var db = db();

        List<Handler> handlers = List.of(
                new GetAllTodosHandler(db),
                new PostTodoHandler(db),
                new DeleteAllTodosHandler(db),
                new GetTodoHandler(db),
                new PatchTodoHandler(db),
                new DeleteTodoHandler(db)
        );

        var rootHandler = new RootHandler(handlers, notFound);

        int port = 7777;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException ignored) {}

        var eventLoop = new EventLoop(
                OptionsBuilder.newBuilder()
                        .withHost("0.0.0.0")
                        .withPort(port)
                        .build(),
                NoopLogger.instance(),
                (request, callback) -> Thread.startVirtualThread(() -> {
                    try {
                        callback.accept(rootHandler.handle(request).intoResponse());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.accept(genericError.intoResponse());
                    }
                })
        );

        System.out.println("Starting server on port " + port);
        eventLoop.start();
        eventLoop.join();
    }
}
