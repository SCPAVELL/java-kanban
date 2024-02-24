package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import task.TaskType;

public class StoringTasks implements TaskManager {

	private int taskId;
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, SubTask> subTasks;
	private final ManagersTaskList managersTaskList;

	public StoringTasks() {

		this.taskId = 0;
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subTasks = new HashMap<>();
		this.managersTaskList = Managers.getHistory();
	}

	@Override
	public ArrayList<Task> getAllTasks(TaskType type) { // Получение списка всех задач.
		if (type == TaskType.EPIC) {
			return new ArrayList<>(epics.values());
		} else if (type == TaskType.SUBTASK) {
			return new ArrayList<>(subTasks.values());
		}
		return new ArrayList<>(tasks.values());
	}

	@Override
	public void removeTasks() { // Удаление всех задач.
		tasks.clear();

	}

	@Override
	public void removeEpics() { // Удаление задачи.
		epics.clear();
		removeSubtasks();
	}

	@Override
	public void removeSubtasks() { // Удаление подзадачи.
		subTasks.clear();
	}

	public ManagersTaskList getManagersTaskList() {
		return managersTaskList;
	}

	@Override
	public List<Task> getHistory() {
		return managersTaskList.getHistory(); // Получить запись событий.
	}

	@Override
	public Task getTask(int taskId) { // Получение задачу по идентификатору.
		Task task = null;
		if (tasks.containsKey(taskId)) {
			task = tasks.get(taskId);
			managersTaskList.add(task);
		} else if (epics.containsKey(taskId)) {
			task = epics.get(taskId);
			managersTaskList.add(task);
		} else if (subTasks.containsKey(taskId)) {
			task = subTasks.get(taskId);
			managersTaskList.add(task);
		}
		return task;
	}

	@Override
	public void createTask(Task task) { // Создание объекта.
		if (task != null) {
			int taskId = generateId(task);
			task.setId(taskId);
			switch (task.getType()) {
			case TASK:
				tasks.put(taskId, task);
				break;
			case EPIC:
				task.setStatus(checkStatus((Epic) task));
				epics.put(taskId, (Epic) task);
				break;
			case SUBTASK:
				subTasks.put(taskId, (SubTask) task);
				SubTask sub = (SubTask) task;
				epics.get(sub.getEpicId()).putSubTask(sub);
				Epic epic = epics.get(sub.getEpicId());
				epics.get(sub.getEpicId()).setStatus(checkStatus(epic));
				break;
			}
		}
	}

	@Override
	public void updateTask(Task task) { // Новая версия задачи.
		if (task == null) {
			return;
		}
		Task removed = this.removeId(task.getId());
		if (removed != null) {
			this.createTask(task);
		}
	}

	@Override
	public Task removeId(int id) { // Удаление по идентификатору.
		Task task = null;
		if (tasks.containsKey(id)) {
			task = tasks.remove(id);
		} else if (epics.containsKey(id)) {
			task = epics.remove(id);
			Epic ep = (Epic) task;
			removeEpicsSubTask(ep.getSubTasks());
		} else if (subTasks.containsKey(id)) {
			task = subTasks.remove(id);
			SubTask sub = (SubTask) task;
			epics.get(sub.getEpicId()).removeSubtask(sub);
		}
		return task;
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

	private int generateId(Task task) {
		if (task == null || task.getId() == 0)
			return generateId();
		return task.getId();
	}

	private int generateId() {
		return ++taskId;
	}

	private TaskStatus checkStatus(Epic epic) { // Узнать статус задачи.
		ArrayList<SubTask> sub = getEpicSubTask(epic);
		if (sub.size() == 0) {
			return TaskStatus.NEW;
		}
		TaskStatus status = sub.get(0).getStatus();
		for (var task : sub) {
			if (task.getStatus() != status) {
				return TaskStatus.IN_PROGRESS;
			}
		}
		return status;
	}

}
