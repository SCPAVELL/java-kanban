package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

	private int taskId = 0;
	private final HashMap<Integer, Task> tasks = new HashMap<>();
	private final HashMap<Integer, Epic> epics = new HashMap<>();
	private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
	private final HistoryManager historyManager;

	public InMemoryTaskManager(HistoryManager historyManager) {
		this.historyManager = historyManager;
	}

	private int generateId() {
		return ++taskId;
	}

	@Override
	public Epic getEpicById(int id) { // получить эпик по ID
		historyManager.add(epics.get(id));
		return epics.get(id);
	}

	@Override
	public SubTask getSubtaskById(int id) { // получить подзадачу по ID
		historyManager.add(subTasks.get(id));
		return subTasks.get(id);
	}

	@Override
	public List<Task> getAllTasks() { // Получение списка всех задач.
		if (tasks.size() == 0) {
			System.out.println("Список задач пуст");
			return Collections.emptyList();
		}
		return new ArrayList<>(tasks.values());
	}

	@Override
	public List<Epic> getAllEpics() { // Получение списка всех эпиков.
		if (epics.size() == 0) {
			System.out.println("Список эпиков пуст");
			return Collections.emptyList();
		}
		return new ArrayList<>(epics.values());
	}

	@Override
	public List<SubTask> getAllSubtasks() { // Получение списка всех подзадач.
		if (subTasks.size() == 0) {
			System.out.println("Список подзадач пуст");
			return Collections.emptyList();
		}
		return new ArrayList<>(subTasks.values());
	}

	@Override
	public void removeTasks() { // Удаление всех задач.
		tasks.clear();
	}

	@Override
	public void removeEpics() { // Удаление задачи.
		subTasks.clear();
		epics.clear();
	}

	@Override
	public void removeSubtasks() { // Удаление подзадачи.
		subTasks.clear();
		for (Epic epic : epics.values()) { // очистка подзадач внутри эпиков и обновление их статусов
			epic.getSubTasks().clear();
			updateTask(epic);
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory(); // Получить запись событий.
	}

	@Override
	public Task getTask(int taskId) { // Получение задачу по идентификатору.
		Task task = null;
		if (tasks.containsKey(taskId)) {
			task = tasks.get(taskId);
			historyManager.add(task);
		} else if (epics.containsKey(taskId)) {
			task = epics.get(taskId);
			historyManager.add(task);
		} else if (subTasks.containsKey(taskId)) {
			task = subTasks.get(taskId);
			historyManager.add(task);
		}
		return task;
	}

	/**
	 * Метод createTask создает новую задачу с уникальным ID, сохраняет ее в
	 * коллекции tasks и возвращает созданный уникальный ID. createEpic создает
	 * новый эпик с уникальным ID, сохраняет ее в коллекции tasks и возвращает
	 * созданный уникальный ID. создает новую подзадачу с уникальным ID. Перед этим
	 * она получает ссылку на эпик по ID, переданному в подзадаче
	 * (subtask.getEpicId()). Если эпик найден, подзадача сохраняется в его списке
	 * подзадач (epic.getSubTasks()) и возвращается созданный уникальный ID. Если
	 * эпик не найден, возвращается ошибка. return -1;
	 */
	@Override
	public int createTask(Task task) {
		int newCreateTaskId = generateId();
		task.setId(newCreateTaskId);
		tasks.put(newCreateTaskId, task);
		return newCreateTaskId;
	}

	@Override
	public int createEpic(Epic epic) {
		int newCreateEpicId = generateId();
		epic.setId(newCreateEpicId);
		epics.put(newCreateEpicId, epic);
		return newCreateEpicId;
	}

	@Override
	public int createSubtask(SubTask subtask) {
		int newCreateSubtaskId = generateId();
		subtask.setId(newCreateSubtaskId);
		Epic epic = epics.get(subtask.getEpicId());
		if (epic != null) {
			subTasks.put(newCreateSubtaskId, subtask);
			epic.setSubtaskId(newCreateSubtaskId);
			checkStatus(epic);
			return newCreateSubtaskId;
		} else {
			System.out.println("Epic не найден");
			return -1;
		}
	}

	@Override
	public void updateTask(Task task) { // Новая версия задачи.
		if (task != null && tasks.containsKey(task.getId())) {
			tasks.put(task.getId(), task);
		} else {
			System.out.println("Task не найдена");
		}
	}

	@Override
	public void updateSubTask(SubTask subtask) { // Новая версия подзадачи.
		if (subtask != null && subTasks.containsKey(subtask.getId())) {

			subTasks.put(subtask.getId(), subtask);
			Epic epic = epics.get(subtask.getEpicId());
			checkStatus(epic);
		} else {
			System.out.println("Subtask не найдена");
		}
	}

	@Override
	public void updateEpic(Epic epic) { // Новая версия эпика
		if (epic != null && epics.containsKey(epic.getId())) {
			epics.put(epic.getId(), epic);
			checkStatus(epic);
		} else {
			System.out.println("Epic не найден");
		}
	}

	@Override
	public Task removeTaskById(int id) { // Удаление по идентификатору.
		Task task = null;
		if (tasks.containsKey(id)) {
			task = tasks.remove(id);
			historyManager.remove(id);
		} else if (epics.containsKey(id)) {
			task = epics.remove(id);
			Epic ep = (Epic) task;
			removeEpicsSubTask(ep.getSubTasks());
			historyManager.remove(id);
		} else if (subTasks.containsKey(id)) {
			task = subTasks.remove(id);
			SubTask sub = (SubTask) task;
			epics.get(sub.getEpicId()).removeSubtask(sub);
			updateTask(sub); // обновлен статус эпика
			historyManager.remove(id);

		} else {
			System.out.println("Задача не найдена!");
		}
		return task;
	}

	public HistoryManager getHistoryManager() {
		return historyManager;
	}

	private void removeEpicsSubTask(ArrayList<SubTask> subTasks) { // Удаление задачи.
		for (var sub : subTasks) {
			this.subTasks.remove(sub.getId());
		}
	}

	@Override
	public ArrayList<SubTask> getEpicSubTask(Epic epic) { // Получение задачи.
		return epic.getSubTasks();
	}

	public void addToHistory(int id) {
		if (epics.containsKey(id)) {
			historyManager.add(epics.get(id));
		} else if (subTasks.containsKey(id)) {
			historyManager.add(subTasks.get(id));
		} else if (tasks.containsKey(id)) {
			historyManager.add(tasks.get(id));
		}
	}

	@Override
	public void checkStatus(Epic epic) { // Пересмотрена логика обновления статуса эпика, с учетом подзадач
		if (epics.containsKey(epic.getId())) {
			if (epic.getSubTasks().size() == 0) {
				epic.setStatus(TaskStatus.NEW);
			} else {
				ArrayList<SubTask> subtasksNew = new ArrayList<>();
				int next = 0;
				int former = 0;

				for (int i = 0; i < epic.getSubTasks().size(); i++) {
					subtasksNew.add(subTasks.get(epic.getSubTasks().get(i)));
				}

				for (SubTask subtask : subtasksNew) {
					if (subtask.getStatus() == TaskStatus.DONE) {
						next++;
					}
					if (subtask.getStatus() == TaskStatus.NEW) {
						former++;
					}
					if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
						epic.setStatus(TaskStatus.IN_PROGRESS);

					}
				}

				if (next == epic.getSubTasks().size()) {
					epic.setStatus(TaskStatus.DONE);
				} else if (former == epic.getSubTasks().size()) {
					epic.setStatus(TaskStatus.NEW);
				} else {
					epic.setStatus(TaskStatus.IN_PROGRESS);
				}
			}
		} else {
			System.out.println("Задача не найденна!");
		}
	}

}
