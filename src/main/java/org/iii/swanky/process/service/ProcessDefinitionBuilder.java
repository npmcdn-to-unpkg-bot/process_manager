package org.iii.swanky.process.service;

import java.util.List;

import org.iii.swanky.process.model.Action;
import org.iii.swanky.process.model.Connection;
import org.iii.swanky.process.model.Constraint;
import org.iii.swanky.process.model.End;
import org.iii.swanky.process.model.Node;
import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.process.model.Split;
import org.iii.swanky.process.model.Start;
import org.iii.swanky.task.model.Task;
import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessDefinitionBuilder {

	static {
		System.setProperty("jbpm.enable.multi.con", "true");
	}

	@Data
	@Builder
	private static class Param {
		ProcessDefinition.ProcessDefinitionBuilder builder;
		Task task;
		Node prev;
	}

	long index;

	public synchronized ProcessDefinition build(Task task) {
		index = 1;
		String id = "ID_" + task.getName();
		String name = "NAME_" + task.getName();
		String packageName = "iii.org.swanky";
		String version = "1.0";

		ProcessDefinition.ProcessDefinitionBuilder builder = ProcessDefinition.builder().id(id).name(name)
				.version(version).packageName(packageName);

		Start start = Start.builder().id(index++).name("Start_" + task.getName()).build();
		builder.start(start);

		Param param = Param.builder().builder(builder).task(task).prev(start).build();
		handleTask(param);

		return builder.build();
	}

	// TODO 設定"jbpm.enable.multi.con"後，是否不需要split?

	// for each task: 1 action + 1 or 2 end(if no next task) + 1 split
	private Node handleTask(Param param) {
		// set task.action
		Action action = Action.builder().id(index++).name("Action_" + param.task.getName()).dialect("java")
				.action(param.task.getAction()).build();
		// set task.condition
		Constraint actionConstraint = Constraint.builder().toNode(action).name("Constraint_" + action.getName())
				.type("code").dialect("java").constraint(param.task.getCondition()).build();

		End splitEnd = End.builder().id(index++).name("End_" + param.task.getName()).build();
		Constraint splitConstraint = Constraint.builder().toNode(splitEnd).name("Split_End_Constraint").type("code")
				.dialect("java").constraint("return true;").build();

		Split s = Split.builder().id(index++).name("Split_" + param.task.getName()).type("or")
				.constraint(actionConstraint).constraint(splitConstraint).build();

		Connection prev_s = Connection.builder().from(param.prev).to(s).build();
		Connection s_a = Connection.builder().from(s).to(action).build();
		Connection s_e = Connection.builder().from(s).to(splitEnd).build();
		param.builder.action(action).end(splitEnd).split(s).connection(prev_s).connection(s_a).connection(s_e);

		List<Task> nextTasks = param.task.getNextTasks();
		if ((nextTasks == null) || (nextTasks.isEmpty())) {
			End aEnd = End.builder().id(index++).name("End_" + action.getName()).build();
			Connection a_aEnd = Connection.builder().from(action).to(aEnd).build();
			param.builder.end(aEnd).connection(a_aEnd);
		} else {
			for (Task nextTask : nextTasks) {
				Param newParam = Param.builder().builder(param.builder).task(nextTask).prev(action).build();
				handleTask(newParam);
			}
		}
		return action;
	}
}
