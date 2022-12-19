package tcore;

import tcore.messages.Packet;

import java.util.ArrayList;

/**
 * Abstract ancestor of primitives meant to control the flow of packets in a
 * concurrency paradigm.
 *
 * @author Pierre-Olivier Talbot
 * @since 2017-12-01
 */
public abstract class ControlPrimitive extends Primitive {

	protected ArrayList<Packet> successList;
	protected ArrayList<Packet> failList;

	/**
	 * Success In.
	 * 
	 * @param p
	 */
	public void successIn(Packet p) {
		successList.add(p);
		isSuccess = false;
	}

	/**
	 * Fail In.
	 * 
	 * @param p
	 */
	public void failIn(Packet p) {
		failList.add(p);
		isSuccess = false;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		successList.clear();
		failList.clear();
		isSuccess = false;
	}
}
