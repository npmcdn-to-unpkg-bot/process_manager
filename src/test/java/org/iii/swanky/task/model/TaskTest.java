package org.iii.swanky.task.model;

import org.junit.Test;

public class TaskTest {
	@Test
	public void createTaskTest() {
		Task task = Task.builder().name("init").condition("device.kw > 100").action("send mail")
				.nextTask(Task.builder().name("next").condition("device.kw > 1000000").action("call 119").build())
				.build();

		System.out.println(task);
	}
}
