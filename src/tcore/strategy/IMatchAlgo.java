package tcore.strategy;

import java.util.ArrayList;

/**
 * Interface defining the Matcher Algorithm strategy
 *
 * @author Sebastien EHouan
 * @since 2020-04-25
 */

import tcore.LHS;
import tcore.Model;
import tcore.messages.Match;

public interface IMatchAlgo {
	
	public ArrayList<Match> match();
	public ArrayList<Match> match_iter(LHS lhs, int max, Model model);
	
}
