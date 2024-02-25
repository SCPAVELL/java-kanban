package manager;

import java.util.ArrayList;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import task.TaskType;

public interface TaskManager {

	Task getTaskById(int id); // получить задачу по ID

	Epic getEpicById(int id); // получить эпик по ID

	SubTask getSubtaskById(int id); // получить подзадачу по ID

	void removeTasks(); // Удаление всех задач.

	void removeEpics(); // Удаление задачи.

	void removeSubtasks(); // Удаление подзадачи.

	Task getTask(int taskId); // Получение задачу по идентификатору.

	void updateTask(Task task); // Новая версия объекта

	Task removeTaskById(int id); // Удаление по идентификатору.

	ArrayList<SubTask> getEpicSubTask(Epic epic); // Получение списка всех подзадач определённого эпика.

	List<Task> getHistory(); // Получить список задач

	List<Task> getAllTasks(); // Получение списка всех задач.

	List<Epic> getAllEpics(); // Получение списка всех эпиков.

	List<SubTask> getAllSubtasks(); // Получение списка всех подзадач.

	void checkStatus(Epic epic); // обновление статуса эпика, с учетом подзадач

	int createTask(Task task); // создает новую задачу с уникальным ID

	int createEpic(Epic epic); // создает новуй эпик с уникальным ID

	int createSubtask(SubTask subtask); // создает новую подзадачу с уникальным ID

}
