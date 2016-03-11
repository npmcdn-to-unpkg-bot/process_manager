package org.iii.swanky.process.service;

import static org.junit.Assert.assertNotNull;

import org.iii.swanky.process.ProcessManagerApp;
import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.task.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ProcessManagerApp.class)
@Slf4j
public class ProcessDefinitionBuilderTest {

	@Autowired
	ProcessDefinitionBuilder builder;

	@Autowired
	RuleFlowProcessBuilder ruleFlowbuilder;

	@Autowired
	ProcessEngine engine;

	@Test
	public void testBuild() throws Exception {
		Task task = Task.builder().name("init").condition("return true;").action("System.out.println(\"Init\");")
				.build();
		ProcessDefinition def = builder.build(task);
		assertNotNull(def);

		log.info(def.toJson());

		engine.runProcess(ruleFlowbuilder.build(def));
	}
}
