package main;

import java.time.Instant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import adapters.InstantAdapter;
import http.KVServer;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

public class Main {

	public static void main(String[] args) {

		KVServer server;
		try {
			Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

			server = new KVServer();
			server.start();
			HistoryManager historyManager = Managers.getDefaultHistory();
			TaskManager httpTaskManager = Managers.getDefault(historyManager);

			Task task1 = new Task("Построить дом", "Дом", TaskStatus.NEW, Instant.now(), 1);
			httpTaskManager.createTask(task1);

			Epic epic1 = new Epic("Купить машину", "Машина", TaskStatus.NEW, Instant.now(), 2);
			httpTaskManager.createEpic(epic1);

			SubTask subtask1 = new SubTask("Список покупок", "Магизин", TaskStatus.NEW, epic1.getId(), Instant.now(),
					3);

			httpTaskManager.getTask(task1.getId());
			httpTaskManager.getEpicById(epic1.getId());
			httpTaskManager.getSubtaskById(subtask1.getId());

			System.out.println("Печать всех задач");
			System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
			System.out.println("Печать всех эпиков");
			System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
			System.out.println("Печать всех подзадач");
			System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
			System.out.println("Загруженный менеджер");
			System.out.println(httpTaskManager);
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}