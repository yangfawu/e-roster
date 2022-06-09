package yangfawu.eroster.payload;

import yangfawu.eroster.exception.InputValidationException;

public abstract class CheckablePayload implements CleanablePayload {

    public abstract void check() throws InputValidationException;

    public void validate() throws InputValidationException {
        clean();
        check();
    }

}
