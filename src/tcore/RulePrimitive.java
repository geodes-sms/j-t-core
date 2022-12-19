package tcore;

import tcore.messages.Packet;

/**
 * Rule Primitive.
 * 
 * @author sebastien.ehouan
 *
 */
public abstract class RulePrimitive extends Primitive {
	/**
	 * @param p
	 * @return
	 */
	public abstract Packet packetIn(Packet p);

}
