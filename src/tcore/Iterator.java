package tcore;

import java.util.ArrayList;
import java.util.Random;

import tcore.messages.Match;
import tcore.messages.MatchSet;
import tcore.messages.Packet;

/**
 * T-Core primitive meant for choosing the next match scheduled for rewriting.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class Iterator extends RulePrimitive {
	/**
	 * Maximum number of iterations allowed.
	 */
	private int maxIterations;

	/**
	 * Iteration counter.
	 */
	private int iteration = 0;

	/**
	 * @param maxIterations
	 */
	public Iterator(int maxIterations) {
		super();
		this.maxIterations = maxIterations;
	}

	/**
	 * {@inheritDoc} Chooses a match to be the next one to rewrite within the
	 * current {@link MatchSet} inside the packet. <br>
	 * Resets iteration count.
	 *
	 * @param p The input packet.
	 * @return The packet with a marked match to rewrite.
	 */
	@Override
	public Packet packetIn(Packet p) {
		MatchSet ms = p.getCurrentMatchSet();
		if (ms == null || ms.getMatches() == null || ms.getMatches().isEmpty()) {
			// TODO: add exception to packet.
			return p;
		} else {
			ms.setMatchToRewrite(choose(ms.getMatches()));
			iteration = 1;
			isSuccess = true;
			exception = null;
			return p;
		}
	}

	/**
	 * {@inheritDoc} Increments the iteration counter.
	 *
	 * @param p The input packet.
	 * @return The output packet containing the next match to rewrite.
	 */
	public Packet nextIn(Packet p) {
		if (iteration > maxIterations) {
			isSuccess = false;
			return p;
		} else {
			MatchSet ms = p.getCurrentMatchSet();
			if (ms == null || ms.getMatches() == null || ms.getMatches().isEmpty()) {
				// TODO: add exception to packet.
				return p;
			} else {
				ms.setMatchToRewrite(choose(ms.getMatches()));
				iteration++;
				return p;
			}
		}
	}

	/**
	 * Chooses a random {@link Match} within a list of matches.
	 *
	 * @param matches List of matches.
	 * @return A random match from the list.
	 */
	protected Match choose(ArrayList<Match> matches) {
		Random r = new Random();
		int i = r.nextInt(matches.size());
		return matches.get(i);
	}

	// TODO: 2017-12-08 What is this supposed to do?
	@SuppressWarnings("unused")
	private void globalizePivots(Packet p) {

	}
}
