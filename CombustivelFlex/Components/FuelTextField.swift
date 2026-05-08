import SwiftUI

struct FuelTextField: View {
    let title: String
    let placeholder: String
    @Binding var text: String
    let tint: Color

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.Spacing.small) {
            Text(title)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(AppTheme.Colors.textSecondary)

            HStack {
                Circle()
                    .fill(tint)
                    .frame(width: 10, height: 10)

                TextField(placeholder, text: $text)
                    .keyboardType(.decimalPad)
                    .textInputAutocapitalization(.never)
            }
        }
    }
}
