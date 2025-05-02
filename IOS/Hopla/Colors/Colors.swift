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
func setupNavigationBar(forDarkMode darkMode: Bool) {
    let appearance = UINavigationBarAppearance()
    
    // 1) Solid background
    appearance.configureWithOpaqueBackground()
    appearance.backgroundEffect = nil
    appearance.backgroundColor = UIColor(
        AdaptiveColor(light: .lightGreen, dark: .darkGreen)
            .color(for: darkMode ? .dark : .light)
    )
    
    // 2) Title colors
    let titleColor = darkMode ? UIColor.white : UIColor.black
    appearance.titleTextAttributes   = [.foregroundColor: titleColor]
    appearance.largeTitleTextAttributes = [.foregroundColor: titleColor]
    
    // 3) Remove shadow
    appearance.shadowColor = nil
    
    // 4) Apply it everywhere
    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance  = appearance
    UINavigationBar.appearance().compactAppearance     = appearance
    if #available(iOS 15.0, *) {
        UINavigationBar.appearance().compactScrollEdgeAppearance = appearance
    }
    
    // 5) Tint (bar button items)
    UINavigationBar.appearance().tintColor = titleColor
}

// MARK: - Tab Bar Colors
func setupTabBarAppearance(forDarkMode darkMode: Bool) {
    let appearance = UITabBarAppearance()
    appearance.configureWithOpaqueBackground()
    
    // 1) Background - dynamic background
    let dynamicBG = UIColor { traits in
        traits.userInterfaceStyle == .dark
        ? UIColor(Color.darkGreen)
        : UIColor(Color.lightGreen)
    }
    appearance.backgroundColor = dynamicBG
    
    // 2) Selected vs. unselected colors
    let selectedColor   = darkMode ? UIColor.black : UIColor.black
    let unselectedColor = darkMode ? UIColor.white : UIColor.white
    
    // Apply to all the item layouts
    let layouts = [
        appearance.stackedLayoutAppearance,
        appearance.inlineLayoutAppearance,
        appearance.compactInlineLayoutAppearance
    ]
    for layout in layouts {
        layout.selected.iconColor             = selectedColor
        layout.selected.titleTextAttributes   = [.foregroundColor: selectedColor]
        layout.normal.iconColor               = unselectedColor
        layout.normal.titleTextAttributes     = [.foregroundColor: unselectedColor]
    }
    
    // 3) Tint fallback
    UITabBar.appearance().tintColor = selectedColor
    
    // 4) Install
    UITabBar.appearance().standardAppearance      = appearance
    if #available(iOS 15.0, *) {
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
}
