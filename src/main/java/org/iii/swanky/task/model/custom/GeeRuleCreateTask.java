package org.iii.swanky.task.model.custom;

import org.iii.swanky.task.model.Task;

import lombok.Builder;
import lombok.Data;

/**
 * Ref: https://docs.google.com/document/d/12Ph6vi7QHg6msuHyDJsU6O-_gHapOKtj-KpBOFxa1Mk/edit#bookmark=id.nyp5h6v35e6p
 */
@Data
@Builder
public class GeeRuleCreateTask implements CustomTask {
	@Override
	public Task toTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
