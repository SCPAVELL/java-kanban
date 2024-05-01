package manager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

	private static int taskId = 0;
	private final HashMap<Integer, Task> tasks = new HashMap<>();
	private final HashMap<Integer, Epic> epics = new HashMap<>();
	private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
	private final HistoryManager historyManager;
	private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
	protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
		prioritizedTasks.clear();
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
			epic.getSubtaskIds().clear();
			prioritizedTasks.remove(subTasks);
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
	public Task createTask(Task task) {
		int newCreateTaskId = generateId();
		task.setId(newCreateTaskId);
		addNewPrioritizedTask(task);
		tasks.put(newCreateTaskId, task);
		return task;
	}

	@Override
	public Epic createEpic(Epic epic) {
		int newCreateEpicId = generateId();
		epic.setId(newCreateEpicId);
		epics.put(newCreateEpicId, epic);
		return epic;
	}

	@Override
	public SubTask createSubtask(SubTask subtask) {
		int newCreateSubtaskId = generateId();
		subtask.setId(newCreateSubtaskId);
		Epic epic = epics.get(subtask.getEpicId());
		if (epic != null) {
			addNewPrioritizedTask(subtask);
			subTasks.put(newCreateSubtaskId, subtask);
			epic.setSubtaskIds(newCreateSubtaskId);
			checkStatus(epic);
			updateTimeEpic(epic);
			return subtask;
		} else {
			System.out.println("Epic не найден");
			return null;
		}
	}

	@Override
	public void updateTask(Task task) { // Новая версия задачи.
		if (task != null && tasks.containsKey(task.getId())) {
			addNewPrioritizedTask(task);
			tasks.put(task.getId(), task);
		} else {
			System.out.println("Task не найдена");
		}
	}

	@Override
	public void updateSubTask(SubTask subtask) { // Новая версия подзадачи.
		if (subtask != null && subTasks.containsKey(subtask.getId())) {
			addNewPrioritizedTask(subtask);
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
	public void deleteTaskById(int id) {
		if (tasks.containsKey(id)) {
			prioritizedTasks.removeIf(task -> task.getId() == id);
			tasks.remove(id);
			historyManager.remove(id);
		} else {
			System.out.println("Task не найден");
		}
	}

	@Override
	public void deleteEpicById(int id) {
		Epic epic = epics.get(id);
		if (epic != null) {
			epic.getSubtaskIds().forEach(subtaskId -> {
				prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
				subTasks.remove(subtaskId);
				historyManager.remove(subtaskId);
			});
			epics.remove(id);
			historyManager.remove(id);
		} else {
			System.out.println("Epic не найден");
		}
	}

	@Override
	public void deleteSubtaskById(int id) {
		SubTask subtask = subTasks.get(id);
		if (subtask != null) {
			Epic epic = epics.get(subtask.getEpicId());
			epic.getSubtaskIds().remove((Integer) subtask.getId());
			checkStatus(epic);
			updateTimeEpic(epic);
			prioritizedTasks.remove(subtask);
			subTasks.remove(id);
			historyManager.remove(id);
		} else {
			System.out.println("Subtask not found");
		}
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
	public List<SubTask> getAllSubtasksByEpicId(int id) {
		if (epics.containsKey(id)) {
			List<SubTask> subtasksNew = new ArrayList<>();
			Epic epic = epics.get(id);
			for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
				subtasksNew.add(subTasks.get(epic.getSubtaskIds().get(i)));
			}
			return subtasksNew;
		} else {
			return Collections.emptyList();
		}
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
			if (epic.getSubtaskIds().size() == 0) {
				epic.setStatus(TaskStatus.NEW);
			} else {
				ArrayList<SubTask> subtasksNew = new ArrayList<>();
				int next = 0;
				int former = 0;

				for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
					subtasksNew.add(subTasks.get(epic.getSubtaskIds().get(i)));
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

				if (next == epic.getSubtaskIds().size()) {
					epic.setStatus(TaskStatus.DONE);
				} else if (former == epic.getSubtaskIds().size()) {
					epic.setStatus(TaskStatus.NEW);
				} else {
					epic.setStatus(TaskStatus.IN_PROGRESS);
				}
			}
		} else {
			System.out.println("Задача не найденна!");
		}
	}

	// обновляет время начала (startTime), время конца (endTime) и длительность
	// (duration) задачи epic на основе времени начала и конца всех подзадач этой
	// эпической задачи.
	public void updateTimeEpic(Epic epic) {
		List<SubTask> subtasks = getAllSubtasks();
		Instant startTime = subtasks.get(0).getStartTime();
		Instant endTime = subtasks.get(0).getEndTime();

		for (SubTask subtask : subtasks) {
			if (subtask.getStartTime().isBefore(startTime))
				startTime = subtask.getStartTime();
			if (subtask.getEndTime().isAfter(endTime))
				endTime = subtask.getEndTime();
		}

		epic.setStartTime(startTime);
		epic.setEndTime(endTime);
		long duration = (endTime.toEpochMilli() - startTime.toEpochMilli());
		epic.setDuration(duration);
	}

	// проверяет, можно ли добавить новую задачу task в список приоритетных задач
	// (prioritizedTasks) без пересечения временных интервалов с другими задачами.
	public boolean checkTime(Task task) {
		if (prioritizedTasks.isEmpty()) {
			return true;
		}
		return prioritizedTasks.stream()
				.allMatch(taskSave -> taskSave.getStartTime() == null || taskSave.getEndTime() == null
						|| task.getStartTime().isAfter(taskSave.getEndTime())
						|| task.getEndTime().isBefore(taskSave.getStartTime()));
	}

	// проверяет временные интервалы между задачами в списке приоритетных задач и
	// выбрасывает исключение, если обнаружено пересечение временных интервалов.
	private void validateTaskPriority() {
		List<Task> tasks = getPrioritizedTasks();
		for (int i = 1; i < prioritizedTasks.size(); i++) {
			Task task = tasks.get(i);
			if (!checkTime(task)) {
				throw new ManagerException(
						"Задачи #" + task.getId() + " и #" + tasks.get(i - 1).getId() + " пересекаются");
			}
		}
	}

	// вызывает метод validateTaskPriority() для проверки временных интервалов между
	// задачами.
	private void addNewPrioritizedTask(Task task) {
		prioritizedTasks.add(task);
		validateTaskPriority();
	}

//возвращает список приоритетных задач (prioritizedTasks) в виде списка.
	private List<Task> getPrioritizedTasks() {
		return prioritizedTasks.stream().toList();
	}

}
