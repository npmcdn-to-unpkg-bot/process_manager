package org.iii.swanky.process_manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Action implements Node {
	long id;
	String name;
	
	/** java or mvel */
	String dialect;
	String action;
}
