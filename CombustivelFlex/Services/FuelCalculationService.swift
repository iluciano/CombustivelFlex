import Foundation

struct FuelCalculationService {
    func calculate(input: FuelCalculationInput) -> FuelCalculationResult {
        let gasolinePrice = NSDecimalNumber(decimal: input.gasolinePrice).doubleValue
        let ethanolPrice = NSDecimalNumber(decimal: input.ethanolPrice).doubleValue
        let recommendedFuel: FuelType = ethanolPrice / gasolinePrice < 0.7 ? .ethanol : .gasoline

        return FuelCalculationResult(recommendedFuel: recommendedFuel)
    }
}
