package org.iii.swanky.process.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Connection {
	Node from;
	Node to;
}
