package org.iii.swanky.process_manager.service;

import org.iii.swanky.process_manager.model.Action;
import org.iii.swanky.process_manager.model.Connection;
import org.iii.swanky.process_manager.model.Constraint;
import org.iii.swanky.process_manager.model.End;
import org.iii.swanky.process_manager.model.Join;
import org.iii.swanky.process_manager.model.Process;
import org.iii.swanky.process_manager.model.Split;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessBuilderService {
	public RuleFlowProcess createRuleFlowProcess(Process process) {
		log.info("[createRuleFlowProcess] Process: " + process);

		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(process.getId());

		factory.name(process.getName()).version(process.getVersion()).packageName(process.getPackageName());
		factory.startNode(process.getStart().getId()).name(process.getStart().getName()).done();

		for (End e : process.getEnds()) {
			factory.endNode(e.getId()).name(e.getName()).done();
		}

		for (Action a : process.getActions()) {
			factory.actionNode(a.getId()).name(a.getName()).action(a.getDialect(), a.getAction()).done();
		}

		for (Split s : process.getSplits()) {
			int splitType = getSplitTypeValue(s);

			SplitFactory splitFactory = factory.splitNode(s.getId()).type(splitType);
			for (Constraint c : s.getConstraints()) {
				splitFactory = splitFactory.constraint(c.getToNode().getId(), c.getName(), c.getType(), c.getDialect(),
						c.getConstraint());
			}
			splitFactory.done();
		}

		for (Join j : process.getJoins()) {
			int joinTypeValue = getJoinTypeValue(j);
			factory.joinNode(j.getId()).name(j.getName()).type(joinTypeValue).done();
		}

		for (Connection c : process.getConnections()) {
			factory.connection(c.getFrom().getId(), c.getTo().getId());
		}

		RuleFlowProcess result = factory.validate().getProcess();
		log.info("[createRuleFlowProcess] RuleFlowProcess result: " + result);
		return result;
	}

	private int getSplitTypeValue(Split split) {
		int splitTypeValue;
		switch (split.getType()) {
		case "and":
			splitTypeValue = org.jbpm.workflow.core.node.Split.TYPE_AND;
			break;
		case "or":
			splitTypeValue = org.jbpm.workflow.core.node.Split.TYPE_OR;
			break;
		case "xand":
			splitTypeValue = org.jbpm.workflow.core.node.Split.TYPE_XAND;
			break;
		case "xor":
			splitTypeValue = org.jbpm.workflow.core.node.Split.TYPE_XOR;
			break;
		default:
			splitTypeValue = org.jbpm.workflow.core.node.Split.TYPE_UNDEFINED;
		}
		return splitTypeValue;
	}

	private int getJoinTypeValue(Join join) {
		int joinTypeValue;
		switch (join.getType()) {
		case "and":
			joinTypeValue = org.jbpm.workflow.core.node.Join.TYPE_AND;
			break;
		case "or":
			joinTypeValue = org.jbpm.workflow.core.node.Join.TYPE_OR;
			break;
		case "xor":
			joinTypeValue = org.jbpm.workflow.core.node.Join.TYPE_XOR;
			break;
		default:
			joinTypeValue = org.jbpm.workflow.core.node.Join.TYPE_UNDEFINED;
		}
		return joinTypeValue;
	}
}
