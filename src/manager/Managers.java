package manager;

public class Managers {

	public static TaskManager getTaskInMemoryManager() {  //поместить задачу в память
        return new StoringTasks();
    }
	
    public static ManagersTaskList getHistory() { // хранит историю задач
        return new InHistoryManager();
    }
}
