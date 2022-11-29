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
		switch (name) {
		case JTCoreConstant.SIMPLEMATCH_ALGORITHM: return new SimpleMatch(lhs, max, model);
		case JTCoreConstant.VF2_ALGORITHM: return new VF2(lhs, max, model);
		default: throw new IllegalArgumentException("Unexpected value: " + name);
		}
	}
}
