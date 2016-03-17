package org.iii.swanky.process;

import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.process.service.ProcessDefinitionBuilder;
import org.iii.swanky.process.service.ProcessEngine;
import org.iii.swanky.process.service.RuleFlowProcessBuilder;
import org.iii.swanky.task.model.Task;
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
	public String addTask(Task task, Model model) {
		ProcessDefinition def = builder.build(task);

		log.info(def.toJson());

		engine.runProcess(ruleFlowbuilder.build(def));
		
		return "new_task";
	}
}
