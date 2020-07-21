package tcore;

import tcore.messages.Packet;

public abstract class RulePrimitive extends Primitive {
	public abstract Packet packetIn(Packet p);
	
}
