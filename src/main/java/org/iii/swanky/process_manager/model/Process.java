package org.iii.swanky.process_manager.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Process {
	String id;
	String name;
	String version;
	String packageName;

	Start start;

	@Singular
	List<End> ends;

	@Singular
	List<Action> actions;

	@Singular
	List<Split> splits;

	@Singular
	List<Join> joins;

	@Singular
	List<Connection> connections;

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
