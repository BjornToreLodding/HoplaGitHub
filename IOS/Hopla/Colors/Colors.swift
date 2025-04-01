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
    
    // Details in brown
    static let lightBrown = Color(hex: "745e4d") // Light
    static let darkBrown = Color(hex: "493d2f") // Dark
    
    // Background
    static let mainLightBackground = Color(hex: "eae6e1") // light mode background
    static let mainDarkBackground = Color(hex: "161818") // dark mode background
    
    // Post colors
    static let lightPostBackground = Color(hex: "FFFFFF") // light
    static let darkPostBackground = Color(hex: "303030") // black
    
    // Green
    static let lightGreen = Color(hex: "456559") // light
    static let darkGreen = Color(hex: "2f463e") // dark
    
    // Text colors if light/dark background
    static let textLightBackground = Color(hex: "000000") // Text on Light background
    static let textDarkBackground = Color(hex: "FFFFFF") // Text on Dark background
    
    // Text colors if green background
    static let lightModeTextOnGreen = Color(hex: "FFFFFF") // Text on Light background
    static let darkModeTextOnGreen = Color(hex: "FFFFFF") // Text on Dark background
    
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
    static let background = AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
    static let text = AdaptiveColor(light: .black, dark: .white)
}

// MARK: - Color Scheme for Navigation Bar
func setupNavigationBar(for colorScheme: ColorScheme) {
    let appearance = UINavigationBarAppearance()
    
    // Set background color based on the color scheme (light/dark)
    let backgroundColor = colorScheme == .dark ? UIColor(Color.darkGreen) : UIColor(Color.lightGreen)

    // Ensure the navbar has a solid color (not transparent)
    appearance.backgroundEffect = nil // This removes any blur effect and sets a solid background color
    
    // Set title text color based on the color scheme
    appearance.titleTextAttributes = [
        .foregroundColor: colorScheme == .dark ? UIColor.white : UIColor.black
    ]
    
    // Set large title text color if you use large titles
    appearance.largeTitleTextAttributes = [
        .foregroundColor: colorScheme == .dark ? UIColor.white : UIColor.black
    ]

    // Apply the appearance to all navigation bars
    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance = appearance
    UINavigationBar.appearance().compactAppearance = appearance

    // For iOS 15+ where navigation bars are more customizable
    if #available(iOS 15.0, *) {
        UINavigationBar.appearance().compactScrollEdgeAppearance = appearance
    }
}

// MARK: - Tab Bar Colors
func setupTabBarAppearance(for colorScheme: ColorScheme) {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        
        // **Tab Bar Background Color**
        appearance.backgroundColor = UIColor(
            AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme)
        )

        // **Selected and Unselected Tab Item Colors**
        let selectedColor = colorScheme == .dark ? UIColor.black : UIColor.black
        let unselectedColor = colorScheme == .dark ? UIColor.lightGray : UIColor(Color.mainLightBackground)

        appearance.stackedLayoutAppearance.selected.iconColor = selectedColor
        appearance.stackedLayoutAppearance.selected.titleTextAttributes = [.foregroundColor: selectedColor]

        appearance.stackedLayoutAppearance.normal.iconColor = unselectedColor
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = [.foregroundColor: unselectedColor]

        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }


