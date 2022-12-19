package tcore;

import tcore.messages.Packet;

import java.util.Stack;

/**
 * T-Core primitive meant for saving packet checkpoints and restoring them.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class Rollbacker extends RulePrimitive {

	private int maxIterations;

	@SuppressWarnings("unused")
	private int iterationsCounter;

	private Stack<Packet> packetStack;

	/**
	 * @param maxIterations
	 */
	public Rollbacker(int maxIterations) {
		if (maxIterations <= 0) {
			throw new IllegalArgumentException("The number of itarations must be greater than 0");
		}
		this.maxIterations = maxIterations;
		packetStack = new Stack<>();
	}

	@Override
	public Packet packetIn(Packet p) {
		establish(new Packet(p));
		if (p.getMatchSets().contains(p.getCurrentMatchSet())) {
			maxIterations = Math.min(p.getCurrentMatchSet().getMatches().size(), maxIterations);
		}
		iterationsCounter = 1;
		isSuccess = true;
		exception = null;
		return p;
	}

	/**
	 * @return
	 */
	public Packet nextIn() {
		// TODO: 2017-12-13 Understand and implement this.
		return null;
	}

	private void establish(Packet p) {
		packetStack.push(p);
	}

	/**
	 * @return
	 */
	public Packet restore() {
		return packetStack.pop();
	}
}
