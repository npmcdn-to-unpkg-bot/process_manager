package org.iii.swanky.process.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.iii.swanky.process.model.Action;
import org.iii.swanky.process.model.Connection;
import org.iii.swanky.process.model.Constraint;
import org.iii.swanky.process.model.End;
import org.iii.swanky.process.model.Join;
import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.process.model.Split;
import org.iii.swanky.process.model.Start;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RuleFlowProcessBuilderTest {

	@Autowired
	RuleFlowProcessBuilder builder;

	@Autowired
	ProcessEngine engine;

	@Test
	public void testBuild() throws Exception {
		ProcessDefinition process = createProcess();
		// ProcessDefinition process = createProcess2();

		log.info("Process toString():\n" + process.toString());
		log.info("Process toJson():\n" + process.toJson());
		RuleFlowProcess jbpmProcess = builder.build(process);
		assertNotNull(jbpmProcess);
		assertTrue(8 == jbpmProcess.getNodes().length);

		engine.addProcess(jbpmProcess);
		RuleFlowProcessInstance instance = engine.startProcess(process.getId());
	}

	private ProcessDefinition createProcess() {
		ProcessDefinition.ProcessDefinitionBuilder builder = ProcessDefinition.builder().id("p_id").name("p_name")
				.version("1.0").packageName("iii.org.swanky");

		Start start = Start.builder().id(1).name("Start").build();
		builder.start(start);

		Action hello = Action.builder().id(2).name("Hello").dialect("java").action(printText("Hello")).build();
		builder.action(hello);
		builder.connection(Connection.builder().from(start).to(hello).build());

		Split.SplitBuilder splitBuilder = Split.builder().id(3).name("Split").type("or");
		Action taskA = Action.builder().id(4).name("TaskA").dialect("java").action(printText("Task A")).build();
		Action taskB = Action.builder().id(5).name("TaskB").dialect("java").action(printText("Task B")).build();
		builder.actions(Arrays.asList(taskA, taskB));

		splitBuilder.constraint(Constraint.builder().toNode(taskA).name("To TaskA").type("code").dialect("java")
				.constraint("return true;").build());
		splitBuilder.constraint(Constraint.builder().toNode(taskB).name("To TaskB").type("code").dialect("java")
				.constraint("return true;").build());
		Split split = splitBuilder.build();
		builder.split(split);
		builder.connection(Connection.builder().from(hello).to(split).build());
		builder.connection(Connection.builder().from(split).to(taskA).build());
		builder.connection(Connection.builder().from(split).to(taskB).build());

		Join join = Join.builder().id(6).name("Join").type("or").build();
		builder.join(join);
		builder.connection(Connection.builder().from(taskA).to(join).build());
		builder.connection(Connection.builder().from(taskB).to(join).build());

		Action goodbye = Action.builder().id(7).name("Goodbye").dialect("java").action(printText("Goodbye")).build();
		builder.action(goodbye);
		builder.connection(Connection.builder().from(join).to(goodbye).build());

		End end = End.builder().id(8).name("End").build();
		builder.end(end);
		builder.connection(Connection.builder().from(goodbye).to(end).build());

		ProcessDefinition process = builder.build();
		return process;
	}

	private ProcessDefinition createProcess2() {
		long index = 1;

		ProcessDefinition.ProcessDefinitionBuilder builder = ProcessDefinition.builder().id("p_id").name("p_name")
				.version("1.0").packageName("iii.org.swanky");

		Start start = Start.builder().id(index++).name("Start").build();
		builder.start(start);

		Action hello = Action.builder().id(index++).name("Hello").dialect("java").action(printText("Hello")).build();
		Constraint helloConstraint = Constraint.builder().toNode(hello).name("Hello_Constraint").type("code")
				.dialect("java").constraint("return true;").build();
		builder.action(hello);

		End helloEnd = End.builder().id(index++).name("Hello_End").build();
		End splitEnd = End.builder().id(index++).name("Split_End").build();
		Constraint splitConstraint = Constraint.builder().toNode(splitEnd).name("Split_Constraint").type("code")
				.dialect("java").constraint("return true;").build();
		builder.end(helloEnd).end(splitEnd);

		Split split = Split.builder().id(index++).name("Split").type("or").constraint(helloConstraint)
				.constraint(splitConstraint).build();
		builder.split(split);

		builder.connection(Connection.builder().from(start).to(split).build());
		builder.connection(Connection.builder().from(split).to(hello).build());
		builder.connection(Connection.builder().from(split).to(splitEnd).build());
		builder.connection(Connection.builder().from(hello).to(helloEnd).build());

		ProcessDefinition process = builder.build();
		return process;
	}

	String printText(String text) {
		return String.format("System.out.println(\"%s\");", text);
	}
}
