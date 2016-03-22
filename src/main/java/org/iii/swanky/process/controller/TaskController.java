package org.iii.swanky.process.controller;

import org.iii.swanky.process.controller.form.TaskForm;
import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.process.service.ProcessDefinitionBuilder;
import org.iii.swanky.process.service.ProcessEngine;
import org.iii.swanky.process.service.RuleFlowProcessBuilder;
import org.iii.swanky.task.model.Task;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/task")
@Slf4j
public class TaskController {
	@Autowired
	ProcessDefinitionBuilder builder;

	@Autowired
	RuleFlowProcessBuilder ruleFlowbuilder;

	@Autowired
	ProcessEngine engine;

	@RequestMapping(method = RequestMethod.GET)
	public String newTask() {
		return "new_task";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String addTask(TaskForm form, Model model) {

		Task.TaskBuilder taskBuilder = Task.builder().name(form.getName()).condition(form.getCondition())
				.action(form.getAction());

		ProcessDefinition def = builder.build(taskBuilder.build());

		log.info(def.toJson());

		RuleFlowProcess process = ruleFlowbuilder.build(def);
		engine.addProcess(process);
		RuleFlowProcessInstance instance = engine.startProcess(process.getId());
		String output = (String) instance.getVariable("output");
		if (output != null) {
			log.info("Output: " + output.toString());
			model.addAttribute("output", output);
		}

		return "new_task";
	}
}
