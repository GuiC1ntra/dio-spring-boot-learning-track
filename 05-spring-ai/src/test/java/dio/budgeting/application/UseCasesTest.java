package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.InvalidTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UseCasesTest {
    private InMemoryTransactionRepository repository;
    private PersistTransactionUseCase persist;
    private ListTransactionsByCategoryUseCase list;
    private SummarizeCategoryUseCase summarize;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        persist = new PersistTransactionUseCase(repository);
        list = new ListTransactionsByCategoryUseCase(repository);
        summarize = new SummarizeCategoryUseCase(repository);
    }

    @Test
    void should_returnValueInReais_when_amountIsStoredInCents() {
        var output = persist.execute(new PersistTransactionInput("Mercado", 1250, Category.GROCERIES));

        assertThat(output.value()).isEqualTo(12.50);
        assertThat(output.category()).isEqualTo("GROCERIES");
    }

    @Test
    void should_listOnlyTransactionsOfTheCategory() {
        persist.execute(new PersistTransactionInput("Mercado", 1000, Category.GROCERIES));
        persist.execute(new PersistTransactionInput("Remédio", 3000, Category.PHARMA));

        assertThat(list.execute(Category.GROCERIES)).hasSize(1);
        assertThat(list.execute(Category.AUTO)).isEmpty();
    }

    @Test
    void should_sumTotalAndCountByCategory() {
        persist.execute(new PersistTransactionInput("Mercado 1", 1000, Category.GROCERIES));
        persist.execute(new PersistTransactionInput("Mercado 2", 2550, Category.GROCERIES));
        persist.execute(new PersistTransactionInput("Remédio", 3000, Category.PHARMA));

        var summary = summarize.execute(Category.GROCERIES);

        assertThat(summary.category()).isEqualTo("GROCERIES");
        assertThat(summary.transactionCount()).isEqualTo(2);
        assertThat(summary.totalInReais()).isEqualTo(35.50);
    }

    @Test
    void should_returnZeroSummary_when_categoryHasNoTransactions() {
        var summary = summarize.execute(Category.LEISURE);

        assertThat(summary.transactionCount()).isZero();
        assertThat(summary.totalInReais()).isZero();
    }

    @Test
    void should_notPersist_when_inputIsInvalid() {
        assertThatThrownBy(() -> persist.execute(new PersistTransactionInput("Mercado", -5, Category.GROCERIES)))
                .isInstanceOf(InvalidTransactionException.class);
        assertThat(list.execute(Category.GROCERIES)).isEmpty();
    }
}
