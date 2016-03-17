package org.iii.swanky.task.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
	String name;
	String condition;
	String action;

	@Singular
	List<Task> nextTasks;
}
