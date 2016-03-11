package org.iii.swanky.process.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Constraint {
	Node toNode;
	String name;
	
	/** code or rule */
	String type;
	
	/** java or mvel */
	String dialect;
	String constraint;
}
