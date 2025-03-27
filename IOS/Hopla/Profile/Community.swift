import SwiftUI

// MARK: - Hike Model
struct Group: Identifiable {
    let id = UUID()
    let name: String
    let filters: [HikeFilter]
    var isFavorite: Bool
    let imageName: String
    let description: String
}

// MARK: - Filter bar Options
enum FilterCommunity: String, CaseIterable, Identifiable {
    case location
    case heart
    
    var id: String { self.rawValue }
    
    var systemImage: String {
        switch self {
        case .location: return "location"
        case .heart: return "heart"
        }
    }
}

// MARK: - Main View
struct Community: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var selectedFilter: FilterCommunity = .location
    @State private var searchText: String = ""
    
    @State private var groups: [Group] = [
        Group(name: "Stall Tønneberg", filters: [.asphalt, .forest, .gravel, .parking], isFavorite: false, imageName: "Group", description: "An easy trail through a beautiful forest. There is parking available at the start and end of the trail."),
        Group(name: "Rogaland Rideklubb", filters: [.mountain, .forest], isFavorite: false, imageName: "Group2", description: "A challenging trail with stunning mountain views. There is no parking available at the start and end of the trail."),
        Group(name: "Barthun stall", filters: [.forest, .gravel, .parking], isFavorite: true, imageName: "Group3", description: "This hike is a paradise for nature lovers. It tends to get very busy during peak season, so it is best to go early in the morning or late in the afternoon."),
        Group(name: "Gjøvik Gård", filters: [.asphalt, .gravel], isFavorite: false, imageName: "Gjøvik", description: "A difficult trail with beautiful views of the valley. It is best to go in the summer when the weather is good.")
    ]
    
    //
    private var filteredGroups: [Group] {
        let lowercasedSearchText = searchText.lowercased()
        return groups.filter { group in
            (selectedFilter == .heart ? group.isFavorite : true) &&
            (searchText.isEmpty || group.name.lowercased().contains(lowercasedSearchText))
        }
    }
    
    var body: some View {
        VStack() {
            filterBar
            searchBar
        }
        .frame(maxWidth: .infinity)
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
        ZStack {
            NavigationView {
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(filteredGroups, id: \ .id) { group in
                            GroupCard(group: binding(for: group))
                        }
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                    }
                    .padding(.horizontal)
                }
                .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
                
            }
            .navigationBarBackButtonHidden(true) // Hides the default back button
        }
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
        .edgesIgnoringSafeArea(.top) // Ensures it can be placed above navigation elements
    }
    
    private func binding(for group: Group) -> Binding<Group> {
        guard let index = groups.firstIndex(where: { $0.id == group.id }) else {
            fatalError("Group not found in list")
        }
        return $groups[index]
    }
    
    // MARK: - Filter Bar
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterCommunity.allCases) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            
        }
        //.frame(height: 60) LEGG TIL
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
    
    // MARK: - Search Bar
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search groups...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}

// MARK: - Hike Card

struct GroupCard: View {
    @Binding var group: Group
    
    var body: some View {
        NavigationLink(destination: CommunityChat(group: group)) {
            VStack {
                ZStack(alignment: .topLeading) {
                    Image(group.imageName)
                        .resizable()
                        .scaledToFill()
                        .frame(width: 370, height: 150)
                        .clipped()
                    
                    Color.black.opacity(0.4)
                        .frame(width: 370, height: 150)
                        .frame(maxWidth: .infinity)
                    
                    HStack {
                        Spacer()
                        Button(action: {
                            group.isFavorite.toggle()
                        }) {
                            Image(systemName: group.isFavorite ? "heart.fill" : "heart")
                                .foregroundColor(group.isFavorite ? .red : .white)
                                .padding()
                        }
                    }
                    
                    VStack {
                        Spacer()
                        HStack {
                            Text(group.name)
                                .foregroundStyle(.white)
                                .padding(.leading, 10)
                            Spacer()
                        }
                    }
                    
                }
                
            }
            .clipShape(Rectangle())
            .shadow(radius: 3)
        }
        .buttonStyle(PlainButtonStyle()) // Removes default navigation link styling
    }
}
