package http.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import adapters.InstantAdapter;
import manager.TaskManager;
import task.SubTask;

//метод возвращает все подзадачи или информацию об одной подзадаче по её идентификатору в формате JSON.
//метода POST создаёт или обновляет подзадачу по запросу.
//метода DELETE удаляет подзадачу по идентификатору.
public class SubtaskHandler implements HttpHandler {
	private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	private final TaskManager taskManager;

	public SubtaskHandler(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		int statusCode;
		String response;

		String method = exchange.getRequestMethod();

		switch (method) {
		case "GET":
			String query = exchange.getRequestURI().getQuery();
			if (query == null) {
				statusCode = 200;
				response = gson.toJson(taskManager.getAllSubtasks());
			} else {
				try {
					int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
					SubTask subtask = taskManager.getSubtaskById(id);
					if (subtask != null) {
						response = gson.toJson(subtask);
					} else {
						response = "Подзадача с данным id не найдена";
					}
					statusCode = 200;
				} catch (StringIndexOutOfBoundsException e) {
					statusCode = 400;
					response = "В запросе отсутствует необходимый параметр id";
				} catch (NumberFormatException e) {
					statusCode = 400;
					response = "Неверный формат id";
				}
			}
			break;
		case "POST":
			String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
			try {
				SubTask subtask = gson.fromJson(bodyRequest, SubTask.class);
				int id = subtask.getId();
				if (taskManager.getSubtaskById(id) != null) {
					taskManager.updateTask(subtask);
					statusCode = 200;
					response = "Подзадача с id=" + id + " обновлена";
				} else {
					System.out.println("CREATED");
					SubTask subtaskCreated = taskManager.createSubtask(subtask);
					System.out.println("CREATED SUBTASK: " + subtaskCreated);
					int idCreated = subtaskCreated.getId();
					statusCode = 201;
					response = "Создана подзадача с id=" + idCreated;
				}
			} catch (JsonSyntaxException e) {
				response = "Неверный формат запроса";
				statusCode = 400;
			}
			break;
		case "DELETE":
			response = "";
			query = exchange.getRequestURI().getQuery();
			if (query == null) {
				taskManager.removeSubtasks();
				;
				statusCode = 200;
			} else {
				try {
					int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
					taskManager.deleteSubtaskById(id);
					statusCode = 200;
				} catch (StringIndexOutOfBoundsException e) {
					statusCode = 400;
					response = "В запросе отсутствует необходимый параметр id";
				} catch (NumberFormatException e) {
					statusCode = 400;
					response = "Неверный формат id";
				}
			}
			break;
		default:
			statusCode = 400;
			response = "Некорректный запрос";
		}

		exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
		exchange.sendResponseHeaders(statusCode, 0);

		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response.getBytes());
		}
	}
}