package tcore.strategy;

import java.util.ArrayList;

/**
 * Interface defining the Matcher Algorithm strategy.
 *
 * @author Sebastien Ehouan
 * @since 2020-04-25
 */

import tcore.LHS;
import tcore.Model;
import tcore.messages.Match;

/**
 * Interface defining MatchAlgo.
 * 
 * @author Sebastien Ehouan
 *
 */
public interface IMatchAlgo {

	/**
	 * Access match for the different algorithms.
	 * 
	 * @return
	 */
	public ArrayList<Match> match();

	/**
	 * Iterate through the matches.
	 * 
	 * @param lhs
	 * @param max
	 * @param model
	 * @return
	 */
	public ArrayList<Match> match_iter(LHS lhs, int max, Model model);

}
