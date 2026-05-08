import SwiftUI

struct StationsView: View {
    var body: some View {
        PlaceholderView(
            title: "Postos proximos",
            message: "Em breve voce podera encontrar postos perto de voce.",
            systemImage: "fuelpump"
        )
        .navigationTitle("Postos")
    }
}

#Preview {
    NavigationStack {
        StationsView()
    }
}
