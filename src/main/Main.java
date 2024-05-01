package main;

import java.io.File;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import manager.FileBackedTaskManager;
import manager.Managers;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import task.TaskType;

public class Main {

	public static void main(String[] args) {

		Path path = Path.of("data.csv");
		File file = new File(String.valueOf(path));
		FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

		System.out.println("!!!Пользовательский сценарий!!!");
		manager.createTask(new Task("Description - 1", "Title - 1", TaskStatus.NEW, Instant.now(), 0));
		manager.createTask(new Task("Description - 2", "Title - 2", TaskStatus.NEW, Instant.now(), 0));
		manager.createEpic(new Epic("Description - 1", "Epic - 1", TaskStatus.NEW, Instant.now(), 0));
		manager.createTask(new Epic("Description - 1", "Epic - 2", TaskStatus.NEW, Instant.now(), 0));
		manager.createSubtask(new SubTask("SubTask - 1", "Description - 1", TaskStatus.DONE, 3));
		manager.createSubtask(new SubTask("SubTask - 2", "Description - 2", TaskStatus.DONE, 3));
		manager.createSubtask(new SubTask("SubTask - 3", "Description - 3", TaskStatus.DONE, 3));

		System.out.println("!!!Запрос созданных задач несколько раз в разном порядке!!!");
		manager.getTask(1);
		manager.getEpicById(3);
		manager.getEpicById(3);
		manager.getEpicById(3);
		manager.getTask(1);
		manager.getEpicById(4);
		manager.getSubtaskById(5);
		manager.getSubtaskById(5);
		manager.getSubtaskById(6);
		manager.getSubtaskById(7);

		System.out.println("!!!История запросов!!!");
		List<Task> history = manager.getHistory();
		System.out.println(history);

		System.out.println("!!!Удаление задачи и подзадачи из истории!!");
		manager.removeTaskById(1);
		manager.removeTaskById(3);

		System.out.println("!!!История запросов после удаления!!!");
		List<Task> historyDeleted = manager.getHistory();
		System.out.println(historyDeleted);

	}
}
