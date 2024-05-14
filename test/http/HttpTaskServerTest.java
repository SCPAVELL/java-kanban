package http;

import adapters.InstantAdapter;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpTaskServerTest {
	private static KVServer kvServer;
	private static HttpTaskServer taskServer;
	private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
	private static final String TASK_BASE_URL = "http://localhost:8080/tasks/task/";
	private static final String EPIC_BASE_URL = "http://localhost:8080/tasks/epic/";
	private static final String SUBTASK_BASE_URL = "http://localhost:8080/tasks/subtask/";

	private int id = 0;

	public int generateId() {
		return ++id;
	}

	@BeforeAll
	static void startServer() {
		try {
			kvServer = new KVServer();
			kvServer.start();
			taskServer = new HttpTaskServer();
			taskServer.start();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	static void stopServer() {
		kvServer.stop();
		taskServer.stop();
	}

	@BeforeEach
	void resetServer() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
			client.send(request, HttpResponse.BodyHandlers.ofString());
			url = URI.create(EPIC_BASE_URL);
			request = HttpRequest.newBuilder().uri(url).DELETE().build();
			client.send(request, HttpResponse.BodyHandlers.ofString());
			url = URI.create(SUBTASK_BASE_URL);
			request = HttpRequest.newBuilder().uri(url).DELETE().build();
			client.send(request, HttpResponse.BodyHandlers.ofString());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	// создаётся новый HTTP-запрос с использованием метода POST для отправки данных
	// на сервер. Если статус равен 200, это означает, что запрос был успешно
	// обработан сервером.
	@Test
	void shouldGetTasks() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		Task task = new Task("description1", "Title1", TaskStatus.NEW, Instant.now(), 1);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

		try {
			client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, response.statusCode());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Метод отправляет запрос на сервер. После этого создаётся новый HTTP-запрос с
	// использованием метода GET для получения данных с сервера.
	@Test
	void shouldGetEpics() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 2);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, response.statusCode());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldGetSubtasksTest() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 3);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int epicId = generateId();
				epic.setId(epicId);
				SubTask subtask = new SubTask("description1", "Title1", TaskStatus.NEW, epic.getId(), Instant.now(), 4);
				url = URI.create(SUBTASK_BASE_URL);

				request = HttpRequest.newBuilder().uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();

				client.send(request, HttpResponse.BodyHandlers.ofString());
				request = HttpRequest.newBuilder().uri(url).GET().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());
				JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	// создаётся новый HTTP-запрос, отправляет этот запрос на сервер. После этого
	// создаётся новый HTTP-запрос с использованием метода GET для получения данных
	// с сервера. Затем клиент отправляет этот запрос на сервер и ожидает ответа.
	@Test
	void shouldGetTaskById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		Task task = new Task("description1", "Title1", TaskStatus.NEW, Instant.now(), 5);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int id = generateId();
				task.setId(id);
				url = URI.create(TASK_BASE_URL + "?id=" + id);
				request = HttpRequest.newBuilder().uri(url).GET().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldGetEpicById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 6);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int id = generateId();
				epic.setId(id);
				url = URI.create(EPIC_BASE_URL + "?id=" + id);
				request = HttpRequest.newBuilder().uri(url).GET().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * shouldGetSubtaskById(), shouldUpdateTask(), shouldUpdateEpic() отправка POST
	 * запросов для создания задачи (Task) и эпика (Epic) с определенными
	 * свойствами. Затем происходит отправка GET запросов для получения созданных
	 * задач и эпиков. Также происходит обновление статуса задачи на IN_PROGRESS и
	 * эпика на IN_PROGRESS, а затем отправка GET запросов для проверки обновления.
	 * 
	 * То есть данные тесты проверяют функциональность создания, получения и
	 * обновления задач и эпиков в системе.
	 */
	@Test
	void shouldGetSubtaskById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 7);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int epicId = generateId();
				epic.setId(epicId);
				SubTask subtask = new SubTask("description1", "Title1", TaskStatus.NEW, epic.getId(), Instant.now(), 8);
				url = URI.create(SUBTASK_BASE_URL);

				request = HttpRequest.newBuilder().uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();
				postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

				assertEquals(201, postResponse.statusCode(), "POST запрос");
				if (postResponse.statusCode() == 201) {
					int id = generateId();
					subtask.setId(id);
					url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
					request = HttpRequest.newBuilder().uri(url).GET().build();
					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					assertEquals(200, response.statusCode());
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldUpdateTask() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		Task task = new Task("description1", "name1", TaskStatus.NEW, Instant.now(), 9);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (postResponse.statusCode() == 201) {
				int id = generateId();
				task.setStatus(TaskStatus.IN_PROGRESS);
				request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
						.build();
				client.send(request, HttpResponse.BodyHandlers.ofString());

				url = URI.create(TASK_BASE_URL + "?id=" + id);
				request = HttpRequest.newBuilder().uri(url).GET().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());

			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldUpdateEpic() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 10);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (postResponse.statusCode() == 201) {
				int id = generateId();
				epic.setStatus(TaskStatus.IN_PROGRESS);
				request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
						.build();
				client.send(request, HttpResponse.BodyHandlers.ofString());

				url = URI.create(EPIC_BASE_URL + "?id=" + id);
				request = HttpRequest.newBuilder().uri(url).GET().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldUpdateSubtask() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 11);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int epicId = generateId();
				epic.setId(epicId);
				SubTask subtask = new SubTask("description1", "Title1", TaskStatus.NEW, epic.getId(), Instant.now(),
						12);
				url = URI.create(SUBTASK_BASE_URL);

				request = HttpRequest.newBuilder().uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();
				postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

				if (postResponse.statusCode() == 201) {
					int id = generateId();
					subtask.setStatus(TaskStatus.IN_PROGRESS);
					request = HttpRequest.newBuilder().uri(url)
							.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();
					client.send(request, HttpResponse.BodyHandlers.ofString());

					url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
					request = HttpRequest.newBuilder().uri(url).GET().build();
					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					assertEquals(200, response.statusCode());
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * тесты проверяют возможность удаления задач и эпиков через HTTP-запросы к
	 * определенному базовому URL.
	 */
	@Test
	void shouldDeleteTasks() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		Task task = new Task("description1", "Title1", TaskStatus.NEW, Instant.now(), 13);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

		try {
			client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).DELETE().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).GET().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldDeleteEpics() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 14);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).DELETE().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).GET().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, response.statusCode());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldDeleteSubtasks() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 15);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int epicId = generateId();
				epic.setId(epicId);
				SubTask subtask = new SubTask("description1", "name1", TaskStatus.NEW, epic.getId(), Instant.now(), 16);
				url = URI.create(SUBTASK_BASE_URL);

				request = HttpRequest.newBuilder().uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();

				client.send(request, HttpResponse.BodyHandlers.ofString());
				request = HttpRequest.newBuilder().uri(url).DELETE().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				request = HttpRequest.newBuilder().uri(url).GET().build();
				assertEquals(200, response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldDeleteTaskById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(TASK_BASE_URL);
		Task task = new Task("description1", "Title1", TaskStatus.NEW, Instant.now(), 17);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			int id = generateId();
			url = URI.create(TASK_BASE_URL + "?id=" + id);
			request = HttpRequest.newBuilder().uri(url).DELETE().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			request = HttpRequest.newBuilder().uri(url).GET().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals("Задача с данным id не найдена", response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldDeleteEpicById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 18);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				int id = generateId();
				url = URI.create(EPIC_BASE_URL + "?id=" + id);
				request = HttpRequest.newBuilder().uri(url).DELETE().build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				request = HttpRequest.newBuilder().uri(url).GET().build();
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals("Эпик с данным id не найден", response.body());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void shouldDeleteSubtaskById() {
		HttpClient client = HttpClient.newHttpClient();
		URI url = URI.create(EPIC_BASE_URL);
		Epic epic = new Epic("description1", "Title1", TaskStatus.NEW, Instant.now(), 15);

		HttpRequest request = HttpRequest.newBuilder().uri(url)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();

		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, postResponse.statusCode(), "POST запрос");
			if (postResponse.statusCode() == 201) {
				SubTask subtask = new SubTask("description1", "Title1", TaskStatus.NEW, epic.getId(), Instant.now(),
						19);
				url = URI.create(SUBTASK_BASE_URL);

				request = HttpRequest.newBuilder().uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask))).build();
				postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

				assertEquals(201, postResponse.statusCode(), "POST запрос");
				if (postResponse.statusCode() == 201) {
					int id = generateId();
					subtask.setId(id);
					url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
					request = HttpRequest.newBuilder().uri(url).DELETE().build();
					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					request = HttpRequest.newBuilder().uri(url).GET().build();
					response = client.send(request, HttpResponse.BodyHandlers.ofString());
					assertEquals("Подзадача с данным id не найдена", response.body());
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}