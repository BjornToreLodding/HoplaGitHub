//
//  ViewController.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/03/2025.
//

import GoogleMaps
import UIKit
import Combine

class ViewController: UIViewController {
    
    private var locationManager = LocationManager()
    private var cancellables = Set<AnyCancellable>()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        GMSServices.provideAPIKey("AIzaSyC-2qlkvP8M1pgfnRMG0rr76SlxaI6jzwQ")
        
        // Create the camera for the initial map position
        let camera = GMSCameraPosition.camera(withLatitude: -33.867, longitude: 151.20, zoom: 6.0)
        
        // Initialize the GMSMapView using init(frame:camera:) method
        
        let options = GMSMapViewOptions()
        let mapView = GMSMapView(options:options) //initialized with default values
        // Enable My Location on the map view directly
        
        mapView.isMyLocationEnabled = true
        
        // Add mapView to the view hierarchy
        self.view.addSubview(mapView)
        
        // Make sure the LocationManager updates the user's location
        locationManager.$userLocation
            .sink { [weak mapView] location in
                guard let location = location else { return }
                
                // Update the map camera to center on the user's location
                let camera = GMSCameraPosition.camera(withLatitude: location.coordinate.latitude,
                                                      longitude: location.coordinate.longitude,
                                                      zoom: 15.0)
                mapView?.camera = camera
            }
            .store(in: &cancellables) // Store the cancellable (to manage memory)
    }
}
