package org.iii.swanky.process_manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.iii.swanky.process_manager.ProcessManagerApp;
import org.iii.swanky.process_manager.model.Action;
import org.iii.swanky.process_manager.model.Connection;
import org.iii.swanky.process_manager.model.Constraint;
import org.iii.swanky.process_manager.model.End;
import org.iii.swanky.process_manager.model.Join;
import org.iii.swanky.process_manager.model.Process;
import org.iii.swanky.process_manager.model.Split;
import org.iii.swanky.process_manager.model.Start;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ProcessManagerApp.class)
@Slf4j
public class ProcessBuilderServiceTest {

	@Autowired
	ProcessBuilderService processBuilderService;

	@Test
	public void testCreateRuleFlowProcess() throws Exception {
		Process process = createProcess();
		log.info("Process toString():\n" + process.toString());
		log.info("Process toJson():\n" + process.toJson());
		RuleFlowProcess jbpmProcess = processBuilderService.createRuleFlowProcess(process);
		assertNotNull(jbpmProcess);
		assertTrue(8 == jbpmProcess.getNodes().length);

		runProcess(jbpmProcess);
	}

	private Process createProcess() {
		Process.ProcessBuilder builder = Process.builder().id("p_id").name("p_name").version("0.1")
				.packageName("iii.org.swanky");

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

		Process process = builder.build();
		return process;
	}

	private void runProcess(RuleFlowProcess process) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource resource = ks.getResources()
				.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
		resource.setSourcePath(process.getId() + ".bpmn2");
		kfs.write(resource);
		ReleaseId releaseId = ks.newReleaseId(process.getName(), process.getPackageName(), process.getVersion());
		kfs.generateAndWritePomXML(releaseId);
		ks.newKieBuilder(kfs).buildAll();
		ks.newKieContainer(releaseId).newKieSession().startProcess(process.getId());
	}

	String printText(String text) {
		return String.format("System.out.println(\"%s\");", text);
	}
}
