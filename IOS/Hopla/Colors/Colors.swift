import SwiftUI

// MARK: - Color Extension for Hex Colors
extension Color {
    /// Custom initializer for hex colors
    init(hex: String) {
        let scanner = Scanner(string: hex)
        scanner.currentIndex = hex.startIndex
        var rgbValue: UInt64 = 0
        scanner.scanHexInt64(&rgbValue)
        
        let red = Double((rgbValue >> 16) & 0xFF) / 255.0
        let green = Double((rgbValue >> 8) & 0xFF) / 255.0
        let blue = Double(rgbValue & 0xFF) / 255.0
        
        self.init(red: red, green: green, blue: blue)
    }
    
    // MARK: - Custom Colors
    static let darkBrown = Color(hex: "583b20")
    static let testBrown = Color(hex: "2e1b0e") // dark mode background
    static let lightBrown = Color(hex: "745e4d")
    static let orange = Color(hex: "e15e0d")
    static let darkGreen = Color(hex: "0b3d2b")
    static let lighterGreen = Color(hex: "195d45")
    static let darkBeige = Color(hex: "cfbfaf")
    static let lightBeige = Color(hex: "eae6e1") // light mode background
    static let black = Color(hex: "000000")
    static let white = Color(hex: "FFFFFF")
}

// MARK: - Adaptive Colors for Light/Dark Mode
struct AdaptiveColor {
    let light: Color
    let dark: Color
    
    func color(for scheme: ColorScheme) -> Color {
        return scheme == .dark ? dark : light
    }
}

// MARK: - Adaptive Text Color Modifier
extension View {
    /// Applies adaptive text colors based on the color scheme
    func adaptiveTextColor(light: Color, dark: Color) -> some View {
        self.modifier(AdaptiveTextColor(light: light, dark: dark))
    }
}

struct AdaptiveTextColor: ViewModifier {
    @Environment(\.colorScheme) var colorScheme
    let light: Color
    let dark: Color
    
    func body(content: Content) -> some View {
        content.foregroundColor(colorScheme == .dark ? dark : light)
    }
}

// MARK: - Predefined Adaptive Colors
extension AdaptiveColor {
    static let background = AdaptiveColor(light: .lightBeige, dark: .testBrown)
    static let text = AdaptiveColor(light: .black, dark: .white)
}

// MARK: - Color Scheme for Navigation Bar
func setupNavigationBar(for colorScheme: ColorScheme) {
    let appearance = UINavigationBarAppearance()
    
    // Set background color based on light/dark mode
    appearance.backgroundColor = UIColor(colorScheme == .dark ? .black : .white)
    
    // Ensure the navbar has a solid color (not transparent)
    appearance.backgroundEffect = UIBlurEffect(style: colorScheme == .dark ? .dark : .light)
    
    // Set text color
    appearance.titleTextAttributes = [
        .foregroundColor: UIColor(colorScheme == .dark ? .white : .black)
    ]
    
    // Apply to all navigation bars
    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance = appearance
    UINavigationBar.appearance().compactAppearance = appearance
}
