package igorluciano.com.br.combustivelflex;

final class CalculationHistoryItem {
    final long createdAt;
    final double gasoline;
    final double ethanol;
    final double gasolineConsumption;
    final double ethanolConsumption;
    final String result;
    final double savings;
    final boolean usedConsumption;

    CalculationHistoryItem(
            long createdAt,
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption,
            String result,
            double savings,
            boolean usedConsumption
    ) {
        this.createdAt = createdAt;
        this.gasoline = gasoline;
        this.ethanol = ethanol;
        this.gasolineConsumption = gasolineConsumption;
        this.ethanolConsumption = ethanolConsumption;
        this.result = result;
        this.savings = savings;
        this.usedConsumption = usedConsumption;
    }
}
