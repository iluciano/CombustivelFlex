import SwiftUI

struct MoreView: View {
    var body: some View {
        List {
            NavigationLink {
                PlaceholderView(
                    title: "Dicas de economia",
                    message: "As dicas serao migradas do app Android nas proximas etapas.",
                    systemImage: "lightbulb"
                )
                .navigationTitle("Dicas")
            } label: {
                FeatureRow(title: "Dicas de economia", subtitle: "Aprenda a economizar combustivel", systemImage: "lightbulb")
            }

            NavigationLink {
                PlaceholderView(
                    title: "Configuracoes",
                    message: "Preferencias de consumo, unidade e notificacoes entram aqui.",
                    systemImage: "gearshape"
                )
                .navigationTitle("Configuracoes")
            } label: {
                FeatureRow(title: "Configuracoes", subtitle: "Personalize suas preferencias", systemImage: "gearshape")
            }
        }
        .navigationTitle("Mais")
    }
}

#Preview {
    NavigationStack {
        MoreView()
    }
}
