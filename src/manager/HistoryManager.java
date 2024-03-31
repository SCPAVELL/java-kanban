package manager;

import java.util.List;

import task.Task;

public interface HistoryManager {

	void add(Task task);

	List<Task> getHistory(); // выполняет функцию "логов" записывает события, с обектами

	void remove(int id);

}
