import SwiftUI

struct HikesDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    let hike: Hike
    
    @State private var userRating: Int = 0

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                HikeTitleView(hike: hike)
                ScrollView {
                    VStack {
                        HikeImageView(hike: hike)
                        //HikeFiltersView(hike: hike)
                        HikeButtonsView()
                        // Add other extracted subviews here
                    }
                    .padding()
                }
                //.background(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen, colorScheme: colorScheme))
            }
            .navigationBarBackButtonHidden(true)
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}


// Title of struct
struct HikeTitleView: View {
    let hike: Hike

    var body: some View {
        VStack {
            Text(hike.name)
                .font(.title)
                .bold()
                .padding()
        }
        .frame(maxWidth: .infinity)
        .frame(height: 40)
    }
}

// Image section
struct HikeImageView: View {
    let hike: Hike

    var body: some View {
        ZStack(alignment: .topLeading) {
            Image(hike.pictureUrl)
                .resizable()
                .scaledToFill()
                .frame(height: 250)
                .clipped()
        }
    }
}

// Filter section
/*
struct HikeFiltersView: View {
    let hike: Hike

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack {
                ForEach(hike.filters) { filter in
                    Text(filter.rawValue)
                        .font(.subheadline)
                        .padding(.horizontal, 5)
                        .padding(.vertical, 5)
                        .background(Color.green.opacity(0.2))
                        .cornerRadius(10)
                }
            }
            .padding(.leading, 5)
        }
        .frame(height: 40)
    }
}
*/
 
// Hikes button
struct HikeButtonsView: View {
    var body: some View {
        HStack {
            NavigationLink(destination: StartHike()) {
                Text("Start hike")
                    .frame(width: 120, height: 50)
            }
            NavigationLink(destination: HikeUpdate()) {
                Text("New update")
                    .frame(width: 120, height: 50)
            }
        }
    }
}

