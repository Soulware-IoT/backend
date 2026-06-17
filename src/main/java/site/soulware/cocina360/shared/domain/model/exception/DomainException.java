package site.soulware.cocina360.shared.domain.model.exception;

public abstract class DomainException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;

    protected DomainException(String messageKey, Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public Object[] getMessageArgs() {
        return this.messageArgs;
    }
}
