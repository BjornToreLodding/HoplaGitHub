//
//  Report.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 15/04/2025.
//

import Foundation
import SwiftUI

class ReportViewModel: ObservableObject {
    @Published var errorMessage: String?
    @Published var successMessage: String?
    
    let reportUrl = "https://hopla.onrender.com/userreports/create"
    
    func submitReport(entityId: String, entityName: String, category: String, message: String) async {
        guard let url = URL(string: reportUrl) else {
            DispatchQueue.main.async {
                self.errorMessage = "Invalid report URL."
            }
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        // If your endpoint requires an authorization token, uncomment:
        if let token = TokenManager.shared.getToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        // Build the JSON body.
        let body: [String: Any] = [
            "EntityId": entityId,
            "EntityName": entityName,
            "Category": category,
            "Message": message
        ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: body, options: [])
            request.httpBody = jsonData
            
            let (data, response) = try await URLSession.shared.data(for: request)
            
            if let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) {
                DispatchQueue.main.async {
                    self.successMessage = "Report submitted successfully!"
                    self.errorMessage = nil
                }
            } else {
                let errorMsg = String(data: data, encoding: .utf8) ?? "Unknown error"
                DispatchQueue.main.async {
                    self.errorMessage = "Report submission failed: \(errorMsg)"
                    self.successMessage = nil
                }
            }
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = "Error: \(error.localizedDescription)"
                self.successMessage = nil
            }
        }
    }
}
