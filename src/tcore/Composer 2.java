package tcore;

/**
 * T-Core primitive meant for encapsulating other T-Core primitives.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public abstract class Composer extends CompositionPrimitive {

    /**
     * Checks if passed module has failed. <br>
     * If so, marks this composer as having failed as well and propagates the exception forward.
     *
     * @param module The module to test for failure.
     * @return true if module failed, false otherwise.
     */
    protected boolean checkModuleForFailure(Primitive module) {
        if (module.isSuccess) {
            return false;
        } else {
            isSuccess = false;
            exception = module.getException();
            return true;
        }
    }
}