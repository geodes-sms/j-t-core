package tcore;

import tcore.messages.Packet;

/**
 * Abstract ancestor of every composer-like T-Core primitive.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public abstract class CompositionPrimitive extends Primitive {

	/**
	 * Receives, processes then returns an initial packet.
	 *
	 * @param p The input packet.
	 * @return The output packet.
	 */
	public abstract Packet packetIn(Packet p);

	/**
	 * Receives, processes then returns a subsequent packet.
	 *
	 * @param p The input packet.
	 * @return The output packet.
	 */
	public abstract Packet nextIn(Packet p);
}
