package org.iii.swanky.task.model.custom;

import org.iii.swanky.task.model.Task;

import lombok.Builder;
import lombok.Data;

/**
 * Ref: https://docs.google.com/document/d/1Xu_70abqiMIfPe7tV5-ijuaUKvAFTW3FdniWCnSF_z4/edit#heading=h.fwomemlv5yy3
 */
@Data
@Builder
public class AddTargetTask implements CustomTask {
	@Override
	public Task toTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
