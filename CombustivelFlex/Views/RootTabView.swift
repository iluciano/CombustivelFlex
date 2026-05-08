import SwiftUI

struct RootTabView: View {
    var body: some View {
        TabView {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                Label(AppTab.home.title, systemImage: AppTab.home.systemImage)
            }

            NavigationStack {
                HistoryView()
            }
            .tabItem {
                Label(AppTab.history.title, systemImage: AppTab.history.systemImage)
            }

            NavigationStack {
                StationsView()
            }
            .tabItem {
                Label(AppTab.stations.title, systemImage: AppTab.stations.systemImage)
            }

            NavigationStack {
                MoreView()
            }
            .tabItem {
                Label(AppTab.more.title, systemImage: AppTab.more.systemImage)
            }
        }
        .tint(AppTheme.Colors.blue)
    }
}

#Preview {
    RootTabView()
}
