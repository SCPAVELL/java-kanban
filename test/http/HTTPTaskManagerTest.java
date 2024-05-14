package http;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerTest;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest<T extends TaskManagerTest<HTTPTaskManager>> {
	private KVServer server;
	private TaskManager manager;

	@BeforeEach
	public void createManager() {
		try {
			server = new KVServer();
			server.start();
			HistoryManager historyManager = Managers.getDefaultHistory();
			manager = Managers.getDefault(historyManager);
		} catch (IOException | InterruptedException e) {
			System.out.println("Ошибка при создании менеджера");
		}
	}

	@AfterEach
	public void stopServer() {
		server.stop();
	}

	// создает два задания (task1 и task2), добавляет их в TaskManager, получает их
	// по их идентификаторам и сравнивает с ожидаемым списком задач.
	@Test
	public void shouldLoadTasks() {
		Task task1 = new Task("description1", "Title1", TaskStatus.NEW, Instant.now(), 0);
		Task task2 = new Task("description2", "Title2", TaskStatus.NEW, Instant.now(), 1);
		manager.createTask(task1);
		manager.createTask(task2);
		manager.getTask(task1.getId());
		manager.getTask(task2.getId());
		List<Task> list = manager.getHistory();
		assertEquals(manager.getAllTasks(), list);
	}

	// создает epic1 и два подзадания (subtask1 и subtask2) связанных с этим
	// эпиком, добавляет их в TaskManager и сравнивает
	@Test
	public void shouldLoadSubtasks() {
		Epic epic1 = new Epic("description1", "name1", TaskStatus.NEW, Instant.now(), 5);
		SubTask subtask1 = new SubTask("description1", "Title1", TaskStatus.NEW, epic1.getId(), Instant.now(), 6);
		SubTask subtask2 = new SubTask("description2", "Title2", TaskStatus.NEW, epic1.getId(), Instant.now(), 7);
		manager.createSubtask(subtask1);
		manager.createSubtask(subtask2);
		manager.getSubtaskById(subtask1.getId());
		manager.getSubtaskById(subtask2.getId());
		List<Task> list = manager.getHistory();
		assertEquals(manager.getAllSubtasks(), list);
	}

}