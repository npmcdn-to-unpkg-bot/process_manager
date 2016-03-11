package org.iii.swanky.process.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class End implements Node {
	long id;
	String name;
}
