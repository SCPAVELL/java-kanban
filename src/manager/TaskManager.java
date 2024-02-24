package manager;

import java.util.ArrayList;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskType;

public interface TaskManager {

	ArrayList<Task> getAllTasks(TaskType type); // Получение списка всех задач.

	void removeTasks(); // Удаление всех задач.

	void removeEpics(); // Удаление задачи.

	void removeSubtasks(); // Удаление подзадачи.

	Task getTask(int taskId); // Получение задачу по идентификатору.

	void createTask(Task task); // Создание. Сам объект должен передаваться в качестве параметра.

	void updateTask(Task task); // Новая версия объекта

	Task removeId(int id); // Удаление по идентификатору.

	ArrayList<SubTask> getEpicSubTask(Epic epic); // Получение списка всех подзадач определённого эпика.

	List<Task> getHistory(); // Получить список задач

}
