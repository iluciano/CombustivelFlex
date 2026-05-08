import SwiftUI

struct HistoryView: View {
    var body: some View {
        PlaceholderView(
            title: "Historico de calculos",
            message: "Os calculos salvos neste aparelho vao aparecer aqui.",
            systemImage: "clock.arrow.circlepath"
        )
        .navigationTitle("Historico")
    }
}

#Preview {
    NavigationStack {
        HistoryView()
    }
}
