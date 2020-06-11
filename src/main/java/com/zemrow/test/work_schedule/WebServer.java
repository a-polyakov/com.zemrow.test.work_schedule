package com.zemrow.test.work_schedule;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.zemrow.test.work_schedule.controller.UserSessionController;
import com.zemrow.test.work_schedule.controller.VacationController;
import com.zemrow.test.work_schedule.controller.WorkScheduleController;
import com.zemrow.test.work_schedule.controller.dto.SessionDto;
import com.zemrow.test.work_schedule.controller.dto.WorkScheduleDto;
import com.zemrow.test.work_schedule.entity.Vacation;
import com.zemrow.test.work_schedule.entity.VacationType;
import com.zemrow.test.work_schedule.entity.WorkSchedule;
import com.zemrow.test.work_schedule.exception.NotAuthorizedException;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Web сервер приложения
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WebServer implements HttpHandler, AutoCloseable {

    //TODO
    private static final int PORT = 8080;

    private final WorkScheduleController workScheduleController;
    private final VacationController vacationController;
    private final UserSessionController userSessionController;

    private final HttpServer server;

    public WebServer(
        WorkScheduleController workScheduleController,
        VacationController vacationController,
        UserSessionController userSessionController
    ) throws IOException {
        this.workScheduleController = workScheduleController;
        this.vacationController = vacationController;
        this.userSessionController = userSessionController;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", this);
    }

    public void start() {
        server.start();
        System.out.println(System.currentTimeMillis() + " ServerSocket start, port:" + PORT);
    }

    @Override public void handle(HttpExchange exchange) throws IOException {
        try {
            final String url = exchange.getRequestURI().toASCIIString();
            if ("/favicon.ico".equals(url)) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            }
            else if ("/".equals(url)) {
                sendStatic(exchange, "/work_schedule.html");
            }
            else if ("/login".equals(url)) {
                final JSONObject json = new JSONObject(new JSONTokener(exchange.getRequestBody()));
                final String username = json.getString("username");
                final String password = json.getString("password");
                final String token = userSessionController.login(username, password);
                final Headers responseHeaders = exchange.getResponseHeaders();
                List<String> values = new ArrayList<>();
                values.add("SESSION=" + token + "; version=1; Path=/; HttpOnly");
                responseHeaders.put("Set-Cookie", values);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            }
            else if (url.startsWith("/work_schedule/") && "GET".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final int yearBegin = 15;
                final int yearEnd = url.indexOf('/', yearBegin);
                if (yearEnd > 0) {
                    int year = Integer.parseInt(url.substring(yearBegin, yearEnd));
                    int month = Integer.parseInt(url.substring(yearEnd + 1));
                    final WorkScheduleDto dto = workScheduleController.getWorkScheduleDto(year, month);
                    sendJson(exchange, new JSONObject(dto));
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                }
            }
            else if ("/work_schedule".equals(url) && "POST".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final JSONObject json = new JSONObject(new JSONTokener(exchange.getRequestBody()));
                final WorkSchedule entity = workScheduleController.insert(json.getLong("userId"), json.getLong("startTime"), json.getLong("endTime"));
                sendJson(exchange, new JSONObject(entity));
            }
            else if (url.startsWith("/work_schedule/") && "DELETE".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final long id = Integer.parseInt(url.substring(15));
                workScheduleController.delete(id);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            }
            else if ("/vacation".equals(url) && "POST".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final JSONObject json = new JSONObject(new JSONTokener(exchange.getRequestBody()));
                final Vacation entity = vacationController.insert(json.getLong("userId"), json.getLong("startTime"), json.getLong("endTime"), VacationType.valueOf(json.getString("type")));
                sendJson(exchange, new JSONObject(entity));
            }
            else if (url.startsWith("/vacation/") && "DELETE".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final long id = Integer.parseInt(url.substring(10));
                vacationController.delete(id);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            }
            else if (url.startsWith("/export/") && "GET".equals(exchange.getRequestMethod())) {
                checkAuth(exchange);
                final int yearBegin = 8;
                final int yearEnd = url.indexOf('/', yearBegin);
                if (yearEnd > 0) {
                    int year = Integer.parseInt(url.substring(yearBegin, yearEnd));
                    int month = Integer.parseInt(url.substring(yearEnd + 1));
                    final Workbook workbook = workScheduleController.export(year, month);
                    sendWorkbook(exchange, workbook);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                }
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            }
        }
        catch (NotAuthorizedException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
        }
        catch (Exception e) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (PrintWriter html = new PrintWriter(outputStream)) {
                e.printStackTrace(html);
            }
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, outputStream.size());
            outputStream.writeTo(exchange.getResponseBody());
        }
        exchange.close();
    }

    private void checkAuth(HttpExchange exchange) throws NotAuthorizedException {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        String token = null;
        for (String cookie : cookies) {
            final int tokenBegin = cookie.indexOf("SESSION=");
            if (tokenBegin > 0 && cookie.length() >= tokenBegin + 81) {
                token = cookie.substring(tokenBegin + 8, tokenBegin + 81);
                break;
            }
        }
        SessionDto session = userSessionController.check(token);
        if (session == null) {
            throw new NotAuthorizedException();
        }
    }

    @Override public void close() throws Exception {
        server.stop(1);
    }

    public static void sendStatic(HttpExchange httpExchange, String resource) throws IOException {
        try (final InputStream inputStream = WebServer.class.getResourceAsStream(resource)) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte buffer[] = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, outputStream.size());
            outputStream.writeTo(httpExchange.getResponseBody());
        }
    }

    public static void sendJson(HttpExchange httpExchange, JSONObject json) throws IOException {
        final ByteArrayOutputStream response = new ByteArrayOutputStream();
        try (OutputStreamWriter responseWriter = new OutputStreamWriter(response, StandardCharsets.UTF_8)) {
            json.write(responseWriter);
        }
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
        response.writeTo(httpExchange.getResponseBody());
    }

    private void sendWorkbook(HttpExchange httpExchange, Workbook workbook) throws IOException {
        final ByteArrayOutputStream response = new ByteArrayOutputStream();
        workbook.write(response);
        httpExchange.getResponseHeaders().set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
        response.writeTo(httpExchange.getResponseBody());
    }

}
