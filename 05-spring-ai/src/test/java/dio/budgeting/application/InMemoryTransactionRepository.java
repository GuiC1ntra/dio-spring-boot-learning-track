package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

/** Repositório em memória usado só nos testes (não precisa de banco nem de Docker). */
class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> storage = new ArrayList<>();

    @Override
    public Transaction save(Transaction transaction) {
        storage.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return storage.stream().filter(t -> t.getCategory() == category).toList();
    }
}
