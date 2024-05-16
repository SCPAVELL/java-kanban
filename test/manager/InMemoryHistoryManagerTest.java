package manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

class InMemoryHistoryManagerTest {

	HistoryManager manager;
	private int id = 0;

	public int generateId() {
		return ++id;
	}

	protected Task createTask() {
		return new Task("Description", "Title", TaskStatus.NEW, Instant.now(), 0);
	}

	@BeforeEach
	public void beforeEach() {
		manager = Managers.getDefaultHistory();
	}

	/*
	 * Метод shouldAddTasksToHistory() проверяет, что задачи добавляются в историю.
	 * Он создает три задачи (task1, task2, task3) и добавляет их в менеджер задач
	 * (manager). Затем он проверяет, что история менеджера содержит все эти три
	 * задачи, используя метод getHistory(). Метод shouldRemoveTask() проверяет, что
	 * задача с заданным идентификатором (в данном случае task2.getId()) удаляется
	 * из менеджера задач. После вызова метода remove() в менеджере задач должны
	 * остаться только задачи task1 и task3.
	 */

	@Test
	public void shouldAddTasksToHistory() {
		Task task1 = createTask();
		int newTaskId1 = generateId();
		task1.setId(newTaskId1);
		Task task2 = createTask();
		int newTaskId2 = generateId();
		task2.setId(newTaskId2);
		Task task3 = createTask();
		int newTaskId3 = generateId();
		task3.setId(newTaskId3);
		manager.add(task1);
		manager.add(task2);
		manager.add(task3);
		assertEquals(List.of(task1, task2, task3), manager.getHistory());
	}

	@Test
	public void shouldRemoveTask() {
		Task task1 = createTask();
		int newTaskId1 = generateId();
		task1.setId(newTaskId1);
		Task task2 = createTask();
		int newTaskId2 = generateId();
		task2.setId(newTaskId2);
		Task task3 = createTask();
		int newTaskId3 = generateId();
		task3.setId(newTaskId3);
		manager.add(task1);
		manager.add(task2);
		manager.add(task3);
		manager.remove(task2.getId());
		assertEquals(List.of(task1, task3), manager.getHistory());
	}

	// Тест проверяет, что после удаления задачи из менеджера задач, история в
	// менеджере становится пустой.
	@Test
	public void shouldRemoveOnlyOneTask() {
		Task task = createTask();
		int newTaskId = generateId();
		task.setId(newTaskId);
		manager.add(task);
		manager.remove(task.getId());
		assertEquals(Collections.EMPTY_LIST, manager.getHistory());
	}

	// Тест проверяет, что если удалить все задачи из менеджера, то его история
	// становится пустой.
	@Test
	public void shouldHistoryIsEmpty() {
		Task task1 = createTask();
		int newTaskId1 = generateId();
		task1.setId(newTaskId1);
		Task task2 = createTask();
		int newTaskId2 = generateId();
		task2.setId(newTaskId2);
		Task task3 = createTask();
		int newTaskId3 = generateId();
		task3.setId(newTaskId3);
		manager.remove(task1.getId());
		manager.remove(task2.getId());
		manager.remove(task3.getId());
		assertEquals(Collections.EMPTY_LIST, manager.getHistory());
	}

	// тест проверяет, что менеджер задач не удаляет задачу с некорректным
	// идентификатором
	@Test
	public void shouldNotRemoveTaskWithBadId() {
		Task task = createTask();
		int newTaskId = generateId();
		task.setId(newTaskId);
		manager.add(task);
		manager.remove(0);
		assertEquals(List.of(task), manager.getHistory());
	}

}
