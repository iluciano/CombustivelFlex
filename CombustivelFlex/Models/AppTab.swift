import Foundation

enum AppTab {
    case home
    case history
    case stations
    case more

    var title: String {
        switch self {
        case .home: return "Inicio"
        case .history: return "Historico"
        case .stations: return "Postos"
        case .more: return "Mais"
        }
    }

    var systemImage: String {
        switch self {
        case .home: return "house"
        case .history: return "clock"
        case .stations: return "fuelpump"
        case .more: return "ellipsis.circle"
        }
    }
}
