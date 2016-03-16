package org.iii.swanky.task.model.custom;

import java.util.List;

import org.iii.swanky.task.model.Task;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class MailTask implements CustomTask {
	String name;
	String condition;

	@Singular
	List<String> mails;

	@Singular
	List<Task> nextTasks;

	@Override
	public Task toTask() {
		String action = "send mail to " + mails;
		return Task.builder().name(name).condition(condition).action(action).nextTasks(nextTasks).build();
	}
}