import SwiftUI

struct HikesDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    let hike: Hike

    @State private var userRating: Int = 0

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                
                // 1. Title
                HikeTitleView(hike: hike)

                // 2. Image
                HikeImageView(hike: hike)

                // 3. Filters
                HikeFiltersView(hike: hike)

                // 4. Buttons
                HikeButtonsView()

                // 5. Description
                Text("Description of trail")

                // 6. Rating
                HStack {
                    Text("Rating:")
                    StarRating(rating: .constant(hike.averageRating))
                }.padding(.horizontal)

                // 7. User rating
                VStack(alignment: .leading) {
                    Text("My rating:")
                    StarRating(rating: $userRating) // You need to make this too
                }.padding(.horizontal)

                // 8. Update box
                Text("Nyeste oppdatering om ruten")
                    .padding(.horizontal)
                    .padding(.vertical)
                    .background(Color.gray.opacity(0.1))
                    .cornerRadius(10)
            }
            .padding(.top)
        }
        .navigationBarTitleDisplayMode(.inline)
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

struct HikeFiltersView: View {
    let hike: Hike

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack {
                if let filters = hike.filters {
                    ForEach(filters, id: \.id) { filter in
                        Text(filter.displayName)
                            .font(.subheadline)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(Color.gray.opacity(0.2))
                            .cornerRadius(8)
                    }
                }

            }
            .padding(.horizontal)
        }
    }
}


 
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

struct StarsView: View {
    let rating: Int

    var body: some View {
        HStack(spacing: 4) {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
            }
        }
    }
}

struct StarsPicker: View {
    @Binding var rating: Int

    var body: some View {
        HStack(spacing: 4) {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
                    .onTapGesture {
                        rating = index
                    }
            }
        }
    }
}
