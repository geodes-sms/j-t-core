package tcore.strategy;

import org.jetbrains.annotations.NotNull;
import tcore.LHS;
import tcore.Model;

/**
 * Factory implementation of SimpleMatch Algorithm
 *
 * @author Sebastien EHouan
 * @since 2020-04-25
 */

public class SimpleMatchFactory implements IMatchAlgoFactory {

	@Override
	public @NotNull IMatchAlgo createMatchAlgo(LHS lhs, int max, Model model) {
		return new SimpleMatch(lhs, max, model);
	}
}
