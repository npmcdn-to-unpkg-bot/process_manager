package org.iii.swanky.process_manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Start implements Node {
	long id;
	String name;
}
