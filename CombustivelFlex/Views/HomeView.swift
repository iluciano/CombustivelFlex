import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: AppTheme.Spacing.large) {
                header

                AppCard {
                    VStack(spacing: AppTheme.Spacing.medium) {
                        FuelTextField(
                            title: "Preco da gasolina",
                            placeholder: "R$ 0,00",
                            text: $viewModel.gasolinePrice,
                            tint: AppTheme.Colors.orange
                        )

                        Divider()

                        FuelTextField(
                            title: "Preco do etanol",
                            placeholder: "R$ 0,00",
                            text: $viewModel.ethanolPrice,
                            tint: AppTheme.Colors.green
                        )
                    }
                }

                AppCard {
                    VStack(alignment: .leading, spacing: AppTheme.Spacing.medium) {
                        Text("Meu carro faz")
                            .font(.headline)
                            .foregroundStyle(AppTheme.Colors.textPrimary)

                        HStack(spacing: AppTheme.Spacing.medium) {
                            FuelTextField(
                                title: "Gasolina",
                                placeholder: "0,0 km/L",
                                text: $viewModel.gasolineConsumption,
                                tint: AppTheme.Colors.orange
                            )

                            FuelTextField(
                                title: "Etanol",
                                placeholder: "0,0 km/L",
                                text: $viewModel.ethanolConsumption,
                                tint: AppTheme.Colors.green
                            )
                        }
                    }
                }

                PrimaryButton(title: "Calcular") {
                    viewModel.calculatePreview()
                }

                if let result = viewModel.previewResult {
                    ResultSummaryView(result: result)
                }
            }
            .padding(AppTheme.Spacing.large)
        }
        .background(AppTheme.Colors.background)
        .navigationTitle("Combustivel Flex")
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: AppTheme.Spacing.small) {
            Text("COMBUSTIVEL FLEX")
                .font(.largeTitle.bold())
                .foregroundStyle(AppTheme.Colors.textPrimary)

            Text("Qual o melhor combustivel para o seu carro?")
                .font(.body)
                .foregroundStyle(AppTheme.Colors.textSecondary)
        }
    }
}

#Preview {
    NavigationStack {
        HomeView()
    }
}
