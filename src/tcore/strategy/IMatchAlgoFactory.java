package tcore.strategy;

import tcore.LHS;
import tcore.Model;

/**
 * Interface defining the methods in the MatchAlgo Factory strategy.
 *
 * @author Sebastien Ehouan
 * @since 2020-04-25
 */

public interface IMatchAlgoFactory {
	/**
	 * @param lhs
	 * @param max
	 * @param model
	 * @return
	 */
	public IMatchAlgo createMatchAlgo(LHS lhs, int max, Model model);
}