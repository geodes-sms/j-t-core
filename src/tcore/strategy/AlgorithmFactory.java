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

public class AlgorithmFactory {

	public static @NotNull IMatchAlgo createMatchAlgo(LHS lhs, int max, Model model, String name) {
		return switch (name) {
		case JTCoreConstant.SIMPLEMATCH_ALGORITHM -> new SimpleMatch(lhs, max, model);
		case JTCoreConstant.VF2_ALGORITHM -> new VF2(lhs, max, model);
		default -> throw new IllegalArgumentException("Unexpected value: " + name);
		};
	}
}
