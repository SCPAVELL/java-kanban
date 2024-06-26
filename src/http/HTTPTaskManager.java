package http;

import adapters.InstantAdapter;
import com.google.gson.*;
import manager.HistoryManager;
import task.Epic;
import task.SubTask;
import task.Task;
import manager.FileBackedTaskManager;
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;
//принимая на вход объект HistoryManager и строку path, и загружает данные из файлов задач, эпиков, подзадач и истории с помощью KVTaskClient.

public class HTTPTaskManager extends FileBackedTaskManager {

	static final String KEY_TASKS = "tasks";
	static final String KEY_SUBTASKS = "subtasks";
	static final String KEY_EPICS = "epics";
	static final String KEY_HISTORY = "history";
	final KVTaskClient client;
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter())
			.create();

	public HTTPTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
		super(historyManager);
		client = new KVTaskClient(path);

		JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
		if (!jsonTasks.isJsonNull()) {
			JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
			for (JsonElement jsonTask : jsonTasksArray) {
				Task task = gson.fromJson(jsonTask, Task.class);
				this.addTask(task);
			}
		}

		JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
		if (!jsonEpics.isJsonNull()) {
			JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
			for (JsonElement jsonEpic : jsonEpicsArray) {
				Epic task = gson.fromJson(jsonEpic, Epic.class);
				this.addEpic(task);
			}
		}

		JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
		if (!jsonSubtasks.isJsonNull()) {
			JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
			for (JsonElement jsonSubtask : jsonSubtasksArray) {
				SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
				this.addSubtask(task);
			}
		}

		JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
		if (!jsonHistoryList.isJsonNull()) {
			JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
			for (JsonElement jsonTaskId : jsonHistoryArray) {
				int taskId = jsonTaskId.getAsInt();
				if (this.subTasks.containsKey(taskId)) {
					this.getSubtaskById(taskId);
				} else if (this.epics.containsKey(taskId)) {
					this.getEpicById(taskId);
				} else if (this.tasks.containsKey(taskId)) {
					this.getTask(taskId);
				}
			}
		}
	}

	// сохраняет текущее состояние задач, эпиков, подзадач и истории в файлы,
	// используя KVTaskClient. Для этого он сериализует значения коллекций tasks,
	// subTasks, epics и истории в формат JSON
	@Override
	public void save() {
		client.put(KEY_TASKS, gson.toJson(tasks.values()));
		client.put(KEY_SUBTASKS, gson.toJson(subTasks.values()));
		client.put(KEY_EPICS, gson.toJson(epics.values()));
		client.put(KEY_HISTORY, gson.toJson(this.getHistory().stream().map(Task::getId).collect(Collectors.toList())));
	}

}