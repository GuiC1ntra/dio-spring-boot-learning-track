package dio.budgeting.application;

import dio.budgeting.application.output.CategorySummaryOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * NOVA FERRAMENTA (melhoria): calcula quanto foi gasto no total em uma categoria.
 * Fica disponível tanto para o REST quanto para o Tool Calling da IA.
 */
@Service
public class SummarizeCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public SummarizeCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "summarize-category",
            description = "Calcula o total gasto e a quantidade de transações de uma categoria")
    public CategorySummaryOutput execute(@ToolParam(description = "Categoria de uma transação") Category category) {
        List<Transaction> transactions = transactionRepository.findAllByCategory(category);
        long totalInCents = transactions.stream().mapToLong(Transaction::getAmount).sum();
        return new CategorySummaryOutput(
                category.name(),
                transactions.size(),
                BigDecimal.valueOf(totalInCents, 2).doubleValue());
    }
}
