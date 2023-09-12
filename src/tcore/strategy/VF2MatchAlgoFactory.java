package tcore.strategy;

import org.jetbrains.annotations.NotNull;

import tcore.LHS;
import tcore.Model;
import tcore.constant.JTCoreConstant;

/**
 * Factory implementation of VF2 Algorithm
 *
 * @author Sebastien Ehouan
 * @since 2020-04-25
 */

public class VF2MatchAlgoFactory implements IMatchAlgoFactory {

	public @NotNull IMatchAlgo createMatchAlgo(LHS lhs, int max, Model model) {
		return new VF2(lhs, max, model);
	}
}
