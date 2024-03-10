package test;

import org.junit.jupiter.api.BeforeEach;

import manager.InMemoryTaskManager;
import manager.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
