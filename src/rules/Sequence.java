package rules;

import tcore.Composer;
import tcore.messages.Packet;

import java.util.ArrayList;

/**
 * Applies each rule in the order provided.
 *
 * @author Pierre-Olivier Talbot
 */
public class Sequence extends Composer {

    @SuppressWarnings("unused")
	private String name;
    private ArrayList<Composer> rules;

    Sequence(String name, ArrayList<Composer> rules) {
        this.name = name;
        this.rules = rules;
    }

    @Override
    public Packet packetIn(Packet p) {
        for (Composer rule : rules) {
            p = rule.packetIn(p);
            if (checkModuleForFailure(rule)) return p;
        }
        isSuccess = true;
        return p;
    }

    // TODO: 2017-12-13 What should this do???
    @Override
    public Packet nextIn(Packet p) {
        return null;
    }
}
