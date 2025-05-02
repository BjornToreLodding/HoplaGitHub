//
//  MapWithRouteView.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 09/04/2025.
//
import SwiftUI
import GoogleMaps

struct MapWithRouteView: UIViewRepresentable {
    // Input: array of custom CoordinateMyHikes structs representing waypoints
    let coordinates: [CoordinateMyHikes]
    // Toggle: whether to draw a trail route (true) or custom coordinates route (false)
    let trailButton: Bool
    // Optional: API response containing trail geometry
    let trailResponse: TrailResponse?
    
    // Creates and configures the GMSMapView instance
    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView()
        
        // Choose which route‐drawing method to use based on the toggle and available data
        if trailButton, let trailResponse = trailResponse {
            drawRouteFromTrail(trailResponse, on: mapView)
        } else {
            drawRouteFromCoordinates(coordinates, on: mapView)
        }
        
        return mapView
    }
    
    // Called whenever SwiftUI updates the view (when new data arrives)
    func updateUIView(_ uiView: GMSMapView, context: Context) {
        uiView.clear() // Remove any existing polylines or markers
        
        if trailButton, let trailResponse = trailResponse {
            drawRouteFromTrail(trailResponse, on: uiView)
        } else {
            drawRouteFromCoordinates(coordinates, on: uiView)
        }
    }
    
    // Draws a polyline on the map using the raw coordinates array
    private func drawRouteFromCoordinates(_ coordinates: [CoordinateMyHikes], on mapView: GMSMapView) {
        guard !coordinates.isEmpty else {
            print("Coordinates are empty – nothing to draw.")
            return
        }
        
        // Build the path from all coordinate points
        let path = GMSMutablePath()
        for coord in coordinates {
            path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
        }
        
        // Create and style the polyline
        let polyline = GMSPolyline(path: path)
        polyline.strokeWidth = 5         // line thickness
        polyline.strokeColor = .blue     // route color for custom coordinates
        polyline.map = mapView           // add to map
        
        // Center the camera on the first coordinate
        if let first = coordinates.first {
            let camera = GMSCameraPosition.camera(
                withLatitude: first.lat,
                longitude: first.lng,
                zoom: 15
            )
            mapView.camera = camera
        }
    }
    
    // Draws a polyline on the map using the TrailResponse data
    private func drawRouteFromTrail(_ trailResponse: TrailResponse, on mapView: GMSMapView) {
        guard !trailResponse.allCoords.isEmpty else {
            print("Trail response contains no coordinates.")
            return
        }
        
        // Build the path from the trailResponse's coordinate list
        let path = GMSMutablePath()
        for coord in trailResponse.allCoords {
            path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
        }
        
        // Create and style the polyline for the trail
        let polyline = GMSPolyline(path: path)
        polyline.strokeWidth = 5         // line thickness
        polyline.strokeColor = .green    // distinct color for trail route
        polyline.map = mapView           // add to map
        
        // Center the camera on the first trail coordinate
        if let first = trailResponse.allCoords.first {
            let camera = GMSCameraPosition.camera(
                withLatitude: first.lat,
                longitude: first.lng,
                zoom: 15
            )
            mapView.camera = camera
        }
    }
}
