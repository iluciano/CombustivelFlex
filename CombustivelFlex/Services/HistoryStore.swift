import Foundation

final class HistoryStore {
    private let maxItems = 25

    func list() -> [CalculationHistoryItem] {
        []
    }

    func save(_ item: CalculationHistoryItem) {
        _ = item
    }

    func clear() {
    }
}
