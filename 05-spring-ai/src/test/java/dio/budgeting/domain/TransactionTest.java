package dio.budgeting.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    @Test
    void should_createTransaction_when_dataIsValid() {
        var transaction = new Transaction("  Mercado do bairro ", 4590, Category.GROCERIES);

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getDescription()).isEqualTo("Mercado do bairro");
        assertThat(transaction.getAmount()).isEqualTo(4590);
        assertThat(transaction.getCategory()).isEqualTo(Category.GROCERIES);
    }

    @Test
    void should_rejectTransaction_when_amountIsNotPositive() {
        assertThatThrownBy(() -> new Transaction("Farmácia", 0, Category.PHARMA))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("maior que zero");
        assertThatThrownBy(() -> new Transaction("Farmácia", -10, Category.PHARMA))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void should_rejectTransaction_when_descriptionIsBlank() {
        assertThatThrownBy(() -> new Transaction("   ", 100, Category.AUTO))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("descrição");
        assertThatThrownBy(() -> new Transaction(null, 100, Category.AUTO))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void should_rejectTransaction_when_categoryIsMissing() {
        assertThatThrownBy(() -> new Transaction("Gasolina", 100, null))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("categoria");
    }
}
