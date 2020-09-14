package tcore.messages;

import tcore.LHS;

import java.util.ArrayList;

/**
 * Cancel message.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public class Cancel {

    // TODO: 2017-12-08 Comprehend and implement.
    @SuppressWarnings("javadoc")
	public ArrayList<LHS> exclusions;

    /**
     * @param exclusions
     */
    public Cancel(ArrayList<LHS> exclusions) {
        this.exclusions = exclusions;
    }
}
