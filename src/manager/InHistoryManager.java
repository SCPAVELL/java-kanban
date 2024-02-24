package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import task.Task;

public class InHistoryManager implements ManagersTaskList {

	private final CustomLinkedList history = new CustomLinkedList();

	@Override
	public void add(Task task) {
		if (task != null) {
			if (history.taskMap.containsKey(task.getId())) {
				remove(task.getId());
			}
			history.taskMap.put(task.getId(), history.linkLast(task));
		}
	}

	@Override
	public List<Task> getHistory() {
		return history.getTasks();
	}

	@Override
	public void remove(int id) {
		if (history.taskMap.containsKey(id)) {
			CustomLinkedListNode delete = history.taskMap.remove(id);
			history.removeNode(delete);
		}
	}

	private static class CustomLinkedList { // класс хранит два поля для хранения первого и последнего узлов списка, а
											// также
											// HashMap для хранения задач по их идентификаторам.
		public CustomLinkedListNode first;
		public CustomLinkedListNode last;
		public Map<Integer, CustomLinkedListNode> taskMap = new HashMap<>();

		private CustomLinkedListNode linkLast(Task data) { // добавляет новый узел в конец списка. Если список пуст,
															// новый узел становится как первым, так и последним.
			CustomLinkedListNode newNode = new CustomLinkedListNode(data);
			if (first == null) {
				first = newNode;
				last = newNode;
				newNode.next = null;
				newNode.former = null;
			} else {
				newNode.former = last;
				newNode.next = null;
				last.next = newNode;
				last = newNode;
			}
			return newNode;
		}

		private List<Task> getTasks() {
			List<Task> allTask = new ArrayList<>();
			CustomLinkedListNode current = first;
			while (current != null) {
				allTask.add(current.task);
				current = current.next;
			}
			return allTask;
		}

		private void removeNode(CustomLinkedListNode delNode) {
			if (delNode.former == null) {
				first = delNode.next;
			}
			if (delNode.next == null) {
				last = delNode.former;
			}
			if (delNode.next != null) {
				delNode.next.former = delNode.former;
			}
			if (delNode.former != null) {
				delNode.former.next = delNode.next;
			}
		}
	}
}
