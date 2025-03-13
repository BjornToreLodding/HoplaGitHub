import SwiftUI

struct HikesDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    let hike: Hike
    
    @State private var userRating: Int = 0  // Store the user's rating
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // This part remains fixed at the top
                VStack {
                    Text(hike.name)
                        .font(.title)
                        .bold()
                        .padding()
                }
                .frame(maxWidth: .infinity)
                .frame(height: 40)
                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                .foregroundColor(AdaptiveColor(light: .white, dark: .white).color(for: colorScheme))
                
                NavigationView {
                    ScrollView {
                        VStack {
                            // MARK: - Image
                            ZStack(alignment: .topLeading) {
                                Image(hike.imageName)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(height: 250)
                                    .clipped()
                            }
                            
                            // MARK: - Filters
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
                            .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            
                            VStack {
                                // MARK: - Buttons
                                HStack {
                                    NavigationLink(destination: StartHike()) {
                                        Text("Start hike")
                                            .frame(width: 120, height: 50)
                                            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                            .foregroundColor(AdaptiveColor(light: .white, dark: .white).color(for: colorScheme))
                                    }
                                    NavigationLink(destination: HikeUpdate()) {
                                        Text("New update")
                                            .frame(width: 120, height: 50)
                                            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                            .foregroundColor(AdaptiveColor(light: .white, dark: .white).color(for: colorScheme))
                                    }
                                }
                                
                                Spacer()
                                
                                VStack {
                                    Text(hike.description)
                                        .fixedSize(horizontal: false, vertical: true) // Allows vertical expansion
                                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                                        .padding(10) // Ensures padding inside the background
                                        .frame(maxWidth: 370) // Keeps the width limited but flexible
                                    
                                }
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                
                                
                                // MARK: - Average Rating
                                HStack {
                                    Text("Average rating: ")
                                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                                        .padding(.leading, 10)
                                    Spacer()
                                    StarRating(rating: .constant(hike.rating))  // Use constant to prevent user changes
                                        .padding(.trailing, 10)
                                }
                                .frame(width: 370, height: 40)
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                
                                Spacer()
                                
                                // MARK: - User Rating
                                HStack {
                                    Text("My rating: ")
                                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                                        .padding(.leading, 10)
                                    Spacer()
                                    StarRating(rating: $userRating)  // User can tap to change rating
                                        .padding(.trailing, 10)
                                }
                                .frame(width: 370, height: 40)
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                
                                
                                Spacer()
                                
                                Text("Newest updates on this hike")
                                    .frame(width: 370, height: 40)
                                    .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                    .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            }
                        }
                        .clipShape(Rectangle())
                        .shadow(radius: 3)
                        .padding()
                        
                    }
                    .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
                }
                .navigationBarBackButtonHidden(true) // Hides the default back button
            }
            .navigationBarTitleDisplayMode(.inline) // Keeps the title in the navigation bar and prevents scrolling
            
            // MARK: - Custom Back Button
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 30, height: 30)
                    }
                    .padding()
                    .foregroundStyle(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    .position(x: 25, y: 20) // Adjust for exact placement
                    
                    Spacer()
                }
                Spacer()
            }
        }
        .edgesIgnoringSafeArea(.top)
    }
}
