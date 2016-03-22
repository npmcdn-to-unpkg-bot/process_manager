package org.iii.swanky.process.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessEngine {
	Map<String, KieSession> kieSessionMap = new LinkedHashMap<String, KieSession>();

	public void addProcess(RuleFlowProcess process) {
		String id = process.getId();
		String name = process.getName();
		String packageName = process.getPackageName();
		String version = process.getVersion();

		log.debug("id:" + id);
		log.debug("name:" + name);
		log.debug("packageName:" + packageName);
		log.debug("version:" + version);

		KieServices ks = KieServices.Factory.get();

		// write to in-memory maven
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource resource = ks.getResources()
				.newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
		resource.setSourcePath(id + ".bpmn2");
		kfs.write(resource);

		// read from in-memory maven
		ReleaseId releaseId = ks.newReleaseId(name, packageName, version);
		kfs.generateAndWritePomXML(releaseId);
		ks.newKieBuilder(kfs).buildAll();

		// create KieContainer -> KieSession
		kieSessionMap.put(id, ks.newKieContainer(releaseId).newKieSession());
	}

	public RuleFlowProcessInstance startProcess(String id) {
		return (RuleFlowProcessInstance) kieSessionMap.get(id).startProcess(id);
	}
}
