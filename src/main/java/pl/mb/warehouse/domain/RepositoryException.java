package pl.mb.warehouse.domain;

public class RepositoryException extends DomainException {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
