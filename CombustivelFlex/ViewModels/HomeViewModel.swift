import Foundation
import Combine

@MainActor
final class HomeViewModel: ObservableObject {
    @Published var gasolinePrice = ""
    @Published var ethanolPrice = ""
    @Published var gasolineConsumption = ""
    @Published var ethanolConsumption = ""
    @Published var previewResult: FuelCalculationResult?

    private let calculationService = FuelCalculationService()

    func calculatePreview() {
        guard
            let gasoline = Decimal(string: normalized(gasolinePrice)),
            let ethanol = Decimal(string: normalized(ethanolPrice)),
            gasoline > 0,
            ethanol > 0
        else {
            previewResult = nil
            return
        }

        let input = FuelCalculationInput(
            gasolinePrice: gasoline,
            ethanolPrice: ethanol,
            gasolineConsumption: Decimal(string: normalized(gasolineConsumption)),
            ethanolConsumption: Decimal(string: normalized(ethanolConsumption))
        )
        previewResult = calculationService.calculate(input: input)
    }

    private func normalized(_ value: String) -> String {
        value.trimmingCharacters(in: .whitespacesAndNewlines).replacingOccurrences(of: ",", with: ".")
    }
}
