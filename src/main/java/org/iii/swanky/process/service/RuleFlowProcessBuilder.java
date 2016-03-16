package org.iii.swanky.process.service;

import org.iii.swanky.process.model.Action;
import org.iii.swanky.process.model.Connection;
import org.iii.swanky.process.model.Constraint;
import org.iii.swanky.process.model.End;
import org.iii.swanky.process.model.Join;
import org.iii.swanky.process.model.ProcessDefinition;
import org.iii.swanky.process.model.Split;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

//TODO 自動產生unique id, Node不需要設定id
@Service
@Slf4j
public class RuleFlowProcessBuilder {
	public RuleFlowProcess build(ProcessDefinition def) {
		log.info("[createRuleFlowProcess] Process: " + def);

		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(def.getId());

		factory.name(def.getName()).version(def.getVersion()).packageName(def.getPackageName());
		factory.startNode(def.getStart().getId()).name(def.getStart().getName()).done();

		for (End e : def.getEnds()) {
			factory.endNode(e.getId()).name(e.getName()).done();
		}

		for (Action a : def.getActions()) {
			factory.actionNode(a.getId()).name(a.getName()).action(a.getDialect(), a.getAction()).done();
		}

		for (Split s : def.getSplits()) {
			int splitType = getSplitTypeValue(s);

			SplitFactory splitFactory = factory.splitNode(s.getId()).type(splitType);
			for (Constraint c : s.getConstraints()) {
				splitFactory = splitFactory.constraint(c.getToNode().getId(), c.getName(), c.getType(), c.getDialect(),
						c.getConstraint());
			}
			splitFactory.done();
		}

		for (Join j : def.getJoins()) {
			int joinTypeValue = getJoinTypeValue(j);
			factory.joinNode(j.getId()).name(j.getName()).type(joinTypeValue).done();
		}

		for (Connection c : def.getConnections()) {
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
