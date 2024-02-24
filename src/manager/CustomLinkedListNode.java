package manager;

import task.Task;

public class CustomLinkedListNode  {
	Task task;
	CustomLinkedListNode next;
	CustomLinkedListNode former;

	public CustomLinkedListNode(Task task){
        this.task = task;
    }
}
