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
    let trailButton: Bool
    let trailResponse: TrailResponse?

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView()

        if trailButton, let trailResponse = trailResponse {
            drawRouteFromTrail(trailResponse, on: mapView)
        } else {
            drawRouteFromCoordinates(coordinates, on: mapView)
        }

        return mapView
    }

    func updateUIView(_ uiView: GMSMapView, context: Context) {
        // ✅ Ensures the map updates when data changes dynamically
        uiView.clear() // Remove old polylines

        if trailButton, let trailResponse = trailResponse {
            drawRouteFromTrail(trailResponse, on: uiView)
        } else {
            drawRouteFromCoordinates(coordinates, on: uiView)
        }
    }

    private func drawRouteFromCoordinates(_ coordinates: [CoordinateMyHikes], on mapView: GMSMapView) {
        guard !coordinates.isEmpty else {
            print("Coordinates are empty")
            return
        }

        let path = GMSMutablePath()
        for coord in coordinates {
            path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
        }

        let polyline = GMSPolyline(path: path)
        polyline.strokeWidth = 5
        polyline.strokeColor = .blue
        polyline.map = mapView

        if let first = coordinates.first {
            let camera = GMSCameraPosition.camera(withLatitude: first.lat, longitude: first.lng, zoom: 15)
            mapView.camera = camera
        }
    }


    private func drawRouteFromTrail(_ trailResponse: TrailResponse?, on mapView: GMSMapView) {
        guard let trailResponse = trailResponse else {
            print("No trail response")
            return
        }

        guard !trailResponse.allCoords.isEmpty else {
            print("No coordinates in trailResponse.allCoords")
            return
        }

        let path = GMSMutablePath()
        for coord in trailResponse.allCoords {
            path.add(CLLocationCoordinate2D(latitude: coord.lat, longitude: coord.lng))
        }

        let polyline = GMSPolyline(path: path)
        polyline.strokeWidth = 5
        polyline.strokeColor = .green
        polyline.map = mapView

        if let first = trailResponse.allCoords.first {
            let camera = GMSCameraPosition.camera(withLatitude: first.lat, longitude: first.lng, zoom: 15)
            mapView.camera = camera
        }
    }

}

