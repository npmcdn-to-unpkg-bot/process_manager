package org.iii.swanky.process.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateRuleFlowProcessTest {

	@Test
	public void testFlow() {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.HelloWorld");
		factory
				// Header
				.name("HelloWorldProcess").version("1.0").packageName("org.jbpm")
				// Nodes
				.startNode(1).name("Start").done().actionNode(2).name("Action")
				.action("java", "System.out.println(\"Hello World\");").done().connection(1, 2)

				.splitNode(3).name("Split").type(Split.TYPE_OR)
				.constraint(4, "to A4 con", "code", "java", "return true;")
				.constraint(5, "to A5 con", "code", "java", "return true;").done().connection(2, 3)

				.actionNode(4).name("Action").action("java", "System.out.println(\"Action 4\");").done().actionNode(5)
				.name("Action").action("java", "System.out.println(\"Action 5\");").done().connection(3, 4)
				.connection(3, 5)

				.joinNode(6).name("Join").type(Join.TYPE_OR).done().connection(4, 6).connection(5, 6)

				// .actionNode(7).name("Action").action("java", "System.out.println(\"Goobye World\");").done()
				.actionNode(7).name("Action")
				.action("java", "kcontext.setVariable(\"var\", kcontext.getVariable(\"var\") + \" GoodBye\");").done()
				.connection(6, 7)

				.endNode(8).name("End").done()
				// Connections
				.connection(7, 8);
		RuleFlowProcess process = factory.validate().getProcess();

		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource resource = ks.getResources()
				.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
		resource.setSourcePath("helloworld.bpmn2");
		kfs.write(resource);
		ReleaseId releaseId = ks.newReleaseId("org.jbpm", "helloworld", "1.0");
		kfs.generateAndWritePomXML(releaseId);
		ks.newKieBuilder(kfs).buildAll();
		KieSession ksession = ks.newKieContainer(releaseId).newKieSession();

		Map<String, Object> param = new LinkedHashMap<String, Object>();
		param.put("var", "init");

		RuleFlowProcessInstance rfpi = (RuleFlowProcessInstance) ksession.startProcess("org.jbpm.HelloWorld", param);

		log.info("vars" + rfpi.getVariables());
		log.info("var:" + rfpi.getVariable("var"));
	}
}
