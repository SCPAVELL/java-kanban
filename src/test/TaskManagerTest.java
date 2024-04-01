package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

abstract class TaskManagerTest<T extends TaskManager> {

	protected T manager;

	protected Task createTask() {
		return new Task("Description", "Title", TaskStatus.NEW, Instant.now(), 0);
	}

	protected Epic createEpic() {

		return new Epic("Description", "Title", TaskStatus.NEW, Instant.now(), 0);
	}

	protected SubTask createSubtask(Epic epic) {
		return new SubTask("Description", "Title", TaskStatus.NEW, epic.getId(), Instant.now(), 0);
	}

	/*
	 * Метод shouldCreateTask() создает задачу, передает ее в метод createTask()
	 * менеджера и проверяет, что статус задачи устанавливается в значение NEW.
	 * Далее проверяется, что в списке всех задач менеджера (метод getAllTasks())
	 * находится только что созданная задача
	 */

	@Test
	public void shouldCreateTask() {
		Task task = createTask();
		manager.createTask(task);
		List<Task> tasks = manager.getAllTasks();
		assertNotNull(task.getStatus());
		assertEquals(TaskStatus.NEW, task.getStatus());
		assertEquals(List.of(task), tasks);
	}

	/*
	 * Метод shouldCreateEpic() создает эпик с помощью createEpic() и затем
	 * сохраняет его в менеджере эпиков . Затем он проверяет, что созданный эпик
	 * доступен в списке всех эпиков и что его статус установлен на NEW. Также метод
	 * проверяет, что у эпика нет подзадач (пустой список subTaskIds) и что в списке
	 * эпиков есть только что созданный эпик.
	 */
	@Test
	public void shouldCreateEpic() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		List<Epic> epics = manager.getAllEpics();
		assertNotNull(epic.getStatus());
		assertEquals(TaskStatus.NEW, epic.getStatus());
		assertEquals(Collections.EMPTY_LIST, epic.getSubTaskIds());
		assertEquals(List.of(epic), epics);
	}

	/*
	 * Метод shouldCreateSubtask() создает сначала эпик, а затем подзадачи с помощью
	 * менеджера задач. Затем он проверяет, что подзадача имеет правильный статус
	 * NEW, что она привязана к нужному эпику и что она присутствует в списке
	 * подзадач. Также метод проверяет, что эпик содержит в списке подзадач только
	 * что созданную подзадачу.
	 */
	@Test
	public void shouldCreateSubtask() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		SubTask subtask = createSubtask(epic);
		manager.createSubtask(subtask);
		List<SubTask> subtasks = manager.getAllSubtasks();
		assertNotNull(subtask.getStatus());
		assertEquals(epic.getId(), subtask.getEpicId());
		assertEquals(TaskStatus.NEW, subtask.getStatus());
		assertEquals(List.of(subtask), subtasks);
		assertEquals(List.of(subtask.getId()), epic.getSubTaskIds());
	}

	/*
	 * Методы создют новую задачу, сохраняют ее в систему, затем изменяет статус
	 * задачи на “IN_PROGRESS” и применяет изменения. В конце он проверяет, что
	 * статус задачи был обновлен успешно
	 */

	@Test
	public void shouldUpdateTaskStatusToInProgress() {
		Task task = createTask();
		manager.createTask(task);
		task.setStatus(TaskStatus.IN_PROGRESS);
		manager.updateTask(task);
		assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
	}

	@Test
	public void shouldUpdateEpicStatusToInProgress() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		epic.setStatus(TaskStatus.IN_PROGRESS);
		assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
	}

	@Test
	public void shouldUpdateTaskStatusToInDone() {
		Task task = createTask();
		manager.createTask(task);
		task.setStatus(TaskStatus.DONE);
		manager.updateTask(task);
		assertEquals(TaskStatus.DONE, manager.getTask(task.getId()).getStatus());
	}

	@Test
	public void shouldUpdateEpicStatusToInDone() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		epic.setStatus(TaskStatus.DONE);
		assertEquals(TaskStatus.DONE, manager.getEpicById(epic.getId()).getStatus());
	}

	@Test
	public void shouldRemoveAllTasks() {
		Task task = createTask();
		manager.createTask(task);
		manager.removeTasks();
		assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
	}

	@Test
	public void shouldRemoveAllEpics() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		manager.removeEpics();
		assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
	}

	@Test
	public void shouldRemoveTaskById() {
		Task task = createTask();
		manager.createTask(task);
		manager.removeTaskById(task.getId());
		assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
	}

	@Test
	public void shouldRemoveEpicById() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		manager.removeTaskById(epic.getId());
		assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
	}

	/*
	 * Методы проверяют, что задача не удаляется, если указан неверный ID. Он
	 * создает задачу, сохраняет ее в менеджере задач, затем пытается удалить задачу
	 * с ID 228. В конце метод проверяет, что в списке задач все еще остается
	 * созданная задача.
	 */
	@Test
	public void shouldNotDeleteTaskIfBadId() {
		Task task = createTask();
		manager.createTask(task);
		manager.removeTaskById(228);
		assertEquals(List.of(task), manager.getAllTasks());
	}

	@Test
	public void shouldNotDeleteEpicIfBadId() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		manager.removeTaskById(228);
		assertEquals(List.of(epic), manager.getAllEpics());
	}

	@Test
	public void shouldNotDeleteSubtaskIfBadId() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		SubTask subtask = createSubtask(epic);
		manager.createSubtask(subtask);
		manager.removeTaskById(228);
		assertEquals(List.of(subtask), manager.getAllSubtasks());
		assertEquals(List.of(subtask.getId()), manager.getEpicById(epic.getId()).getSubTaskIds());
	}

	/*
	 * Метод проверяет, что удаление всех задач и удаление конкретной задачи с
	 * идентификатором 228 (если такая существует) приводит к тому, что размер
	 * списка всех задач становится равным 0.
	 */

	@Test
	public void shouldDoNothingIfTaskHashMapIsEmpty() {
		manager.removeTasks();
		manager.removeTaskById(999);
		assertEquals(0, manager.getAllTasks().size());
	}

	@Test
	public void shouldDoNothingIfEpicHashMapIsEmpty() {
		manager.removeEpics();
		manager.removeTaskById(999);
		assertTrue(manager.getAllEpics().isEmpty());
	}

	@Test
	public void shouldDoNothingIfSubtaskHashMapIsEmpty() {
		manager.removeEpics();
		manager.removeTaskById(999);
		assertEquals(0, manager.getAllSubtasks().size());
	}

	@Test
	public void shouldReturnEmptyHistory() {
		assertEquals(Collections.EMPTY_LIST, manager.getHistory());
	}

	@Test
	public void shouldReturnEmptyHistoryIfTasksNotExist() {
		manager.getTask(999);
		manager.getSubtaskById(999);
		manager.getEpicById(999);
		assertTrue(manager.getHistory().isEmpty());
	}

	@Test
	public void shouldReturnHistoryWithTasks() {
		Epic epic = createEpic();
		manager.createEpic(epic);
		SubTask subtask = createSubtask(epic);
		manager.createSubtask(subtask);
		manager.getEpicById(epic.getId());
		manager.getSubtaskById(subtask.getId());
		List<Task> list = manager.getHistory();
		assertEquals(2, list.size());
		assertTrue(list.contains(subtask));
		assertTrue(list.contains(epic));
	}

}
