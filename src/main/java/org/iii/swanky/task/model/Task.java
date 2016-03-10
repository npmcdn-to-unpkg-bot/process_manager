package org.iii.swanky.task.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Task {
	String name;
	String condition;
	String action;

	@Singular
	List<Task> nextTasks;
}
