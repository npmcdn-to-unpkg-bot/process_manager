package org.iii.swanky.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Device {
	String id;
	double kw;
}