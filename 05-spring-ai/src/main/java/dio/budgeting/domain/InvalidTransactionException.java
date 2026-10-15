package dio.budgeting.domain;

/** Lançada quando uma transação viola as regras de negócio. */
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
