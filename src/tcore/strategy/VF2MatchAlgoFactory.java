package tcore.strategy;

import org.jetbrains.annotations.NotNull;
import tcore.LHS;
import tcore.Model;

/**
 * Factory implementation of VF2 Algorithm
 *
 * @author Sebastien EHouan
 * @since 2020-04-25
 */
public class VF2MatchAlgoFactory implements IMatchAlgoFactory {

	@Override
	public @NotNull IMatchAlgo createMatchAlgo(LHS lhs, int max, Model model) {
		return new VF2();
	}
}
