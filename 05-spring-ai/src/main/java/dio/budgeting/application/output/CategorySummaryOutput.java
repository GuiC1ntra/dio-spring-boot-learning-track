package dio.budgeting.application.output;

public record CategorySummaryOutput(String category, int transactionCount, double totalInReais) {
}
