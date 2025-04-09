//
//  MapWithRouteView.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 09/04/2025.
//

import SwiftUI
import GoogleMaps

struct MapWithRouteView: UIViewRepresentable {
    let coordinates: [CoordinateMyHikes]
    let trailButton: Bool  // Add the trailButton state to the view

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView()

        if trailButton {
            // Use the map for the trail
            let path = GMSMutablePath()
            for coord in coordinates {
                path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
            }
            let polyline = GMSPolyline(path: path)
            polyline.strokeWidth = 5
            polyline.strokeColor = .blue
            polyline.map = mapView

            // Optional: center map for the trail
            if let first = coordinates.first {
                let camera = GMSCameraPosition.camera(withLatitude: first.lat, longitude: first.lng, zoom: 15)
                mapView.camera = camera
            }
        } else {
            // Use an alternative map or configuration when the button is false
            let camera = GMSCameraPosition.camera(withLatitude: 60.7925, longitude: 10.7405, zoom: 10) // Example coordinates for another map view
            mapView.camera = camera
        }

        return mapView
    }

    func updateUIView(_ uiView: GMSMapView, context: Context) {
        // Optionally update the map if needed (e.g., reload coordinates or switch map)
        if trailButton {
            let path = GMSMutablePath()
            for coord in coordinates {
                path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
            }
            let polyline = GMSPolyline(path: path)
            polyline.strokeWidth = 5
            polyline.strokeColor = .blue
            polyline.map = uiView
        } else {
            // Handle any updates for the alternative map
        }
    }
}


