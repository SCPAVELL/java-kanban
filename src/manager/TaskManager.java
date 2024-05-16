package manager;

import java.util.List;
import task.Epic;
import task.SubTask;
import task.Task;

public interface TaskManager {

	Epic getEpicById(int id); // получить эпик по ID

	SubTask getSubtaskById(int id); // получить подзадачу по ID

	void removeTasks(); // Удаление всех задач.

	void removeEpics(); // Удаление задачи.

	void removeSubtasks(); // Удаление подзадачи.

	Task getTask(int taskId); // Получение задачу по идентификатору.

	void updateTask(Task task); // Новая версия объекта

	void updateSubTask(SubTask subtask); // Новая версия подзадачи

	void updateEpic(Epic epic); // Новая версия эпика

	List<SubTask> getAllSubtasksByEpicId(int id); // Получение списка всех подзадач определённого эпика.

	List<Task> getHistory(); // Получить список задач

	List<Task> getAllTasks(); // Получение списка всех задач.

	List<Epic> getAllEpics(); // Получение списка всех эпиков.

	List<SubTask> getAllSubtasks(); // Получение списка всех подзадач.

	void checkStatus(Epic epic); // обновление статуса эпика, с учетом подзадач

	Task createTask(Task task); // создает новую задачу с уникальным ID

	Epic createEpic(Epic epic); // создает новуй эпик с уникальным ID

	SubTask createSubtask(SubTask subtask); // создает новую подзадачу с уникальным ID

	void deleteTaskById(int id);

	void deleteEpicById(int id);

	void deleteSubtaskById(int id);

	List<Task> getPrioritizedTasks();

}