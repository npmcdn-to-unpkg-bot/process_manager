package org.iii.swanky.process_manager.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Split implements Node {
	long id;
	String name;
	String type;
	
	@Singular
	List<Constraint> constraints = new ArrayList<Constraint>();
}
