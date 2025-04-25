//
//  MyHikesDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 07/04/2025.
//

import SwiftUI
import Combine
import UIKit

struct CoordinateMyHikes: Codable {
    let lat: Double
    let lng: Double
    
    private enum CodingKeys: String, CodingKey {
        case lat = "latMean"
        case lng = "longMean"
    }
}

struct TrailResponse: Codable {
    let id: String
    let distance: Double
    let allCoords: [CoordinateMyHikes]
}

struct FilterPicker: View {
    @Binding var selectedFilters: [String]
    
    let availableFilters = [
        "Grus", "3", "true" // Example filter options
    ]
    
    var body: some View {
        ForEach(availableFilters, id: \.self) { filter in
            Button(action: {
                if selectedFilters.contains(filter) {
                    selectedFilters.removeAll { $0 == filter }
                } else {
                    selectedFilters.append(filter)
                }
            }) {
                HStack {
                    Text(filter)
                    if selectedFilters.contains(filter) {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.green)
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.2))
                .cornerRadius(8)
            }
        }
    }
}

struct MyHikesDetails: View {
  @Binding var myHikes: [MyHike]
  @Environment(\.dismiss) private var dismiss

  // Create and own the view-model for this screen:
  @StateObject private var vm: MyHikesDetailsViewModel
    

  // Local UI state:
  @State private var isEditing     = false
  @State private var formVisible   = false
  @State private var newTitle      = ""

  init(hike: MyHike, myHikes: Binding<[MyHike]>) {
    _myHikes = myHikes
    // initialize the StateObject with the incoming hike
    _vm       = StateObject(wrappedValue: MyHikesDetailsViewModel(hike: hike))
  }

  var body: some View {
    NavigationStack {
      // our own back button
      MyHikesDetailsHeader { dismiss() }

        /*
      ScrollView {
        // read-only vs edit form
        if isEditing {
          HikeEditSection(
            trailName:       $vm.hike.trailName,
            title:           $vm.hike.title,
            description:     $vm.hike.comment,
            selectedHorseId: $vm.hike.horseName,
            onSave:          saveEdits,
            onCancel:        { isEditing = false },
            horses:          vm.horses
          )
        } else {
          HikeReadOnlySection(hike: vm.hike)
            .toolbar {
              Button { isEditing.toggle() }
                 label: { Image(systemName: "pencil") }
            }
        }

        // upgrade section
          HikeUpgradeSection(
            formVisible:     $formVisible,
            selectedImage:   $vm.selectedImage,
            newTitle:        $vm.hike.title,
            selectedFilters: $vm.selectedFilters,
            onSubmit: {
              vm.upgradeToTrail(
                filters: vm.selectedFilters,
                completion: { success in
                  if success {
                    formVisible = false
                  }
                }
              )
            }
          )

        // map & route
        MapWithRouteView(
          coordinates:   vm.coordinates,
          trailButton:   vm.hike.trailButton,
          trailResponse: vm.trailResponse
        )
        .frame(height: 300)
        .cornerRadius(12)
      }
         */
      .onAppear {
        vm.fetchDetails()
        vm.fetchCoordinates()
        vm.fetchHorses()
      }
    }
  }

  private func saveEdits() {
    // call through to VM
    vm.updateHike(
      trailName:   vm.hike.trailName,
      title:       vm.hike.title,
      description: vm.hike.comment,
      horseId:     vm.hike.horseName
    )
    // also update your parent array:
    if let idx = myHikes.firstIndex(where: { $0.id == vm.hike.id }) {
      myHikes[idx] = vm.hike
    }
    isEditing = false
  }
}


struct HeaderViewMyHikesDetails: View {
    let name: String
    let colorScheme: ColorScheme
    let onBack: () -> Void
    
    var body: some View {
        ZStack {
            AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                .color(for: colorScheme)
                .frame(maxWidth: .infinity)
            
            // Centered Title
            Text(name)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.white)
            
            // Back button aligned leading
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 24, height: 24)
                        .foregroundStyle(
                            AdaptiveColor(light: .lightModeTextOnGreen,
                                          dark: .darkModeTextOnGreen)
                            .color(for: colorScheme)
                        )
                }
                Spacer()
            }
            .padding(.horizontal, 16)
        }
    }
}

struct MyHikesDetailsHeader: View {
    let onBack: ()->Void
    var body: some View {
        HStack {
            Button(action: onBack) {
                Image(systemName: "chevron.left")
            }
            Spacer()
            Text("Details").font(.headline)
            Spacer()
        }
        .padding()
    }
}

struct HikeReadOnlySection: View {
    let hike: MyHike

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Image
            AsyncImage(url: URL(string: hike.pictureUrl ?? "")) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .frame(height: 200)
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFit()
                        .frame(height: 200)
                case .failure:
                    // Fallback image or color
                    Color.gray
                        .frame(height: 200)
                @unknown default:
                    EmptyView()
                }
            }

            // Details
            Text("Trail: \(hike.trailName)")
                .font(.title2)
            Text("Title: \(hike.title)")
            Text("Comment: \(hike.comment)")
            if let horse = hike.horseName, !horse.isEmpty {
                Text("Horse: \(horse)")
            }

            // Metrics (customize formatting)
            Text("Distance: \(String(format: "%.2f km", hike.length))")
            Text("Duration: \(String(format: "%02d:%02d", Int(hike.duration) / 60, Int(hike.duration) % 60))")
        }
        .padding()
    }
}

struct HikeEditSection: View {
    @Binding var trailName: String
    @Binding var title: String
    @Binding var description: String
    @Binding var selectedHorseId: String?
    let onSave: ()->Void
    let onCancel: ()->Void
    let horses: [Horse]
    
    var body: some View {
        Form {
            TextField("Trail name", text: $trailName)
            TextField("Title",      text: $title)
            TextField("Comment",    text: $description)
            SwiftUI.Picker("Horse", selection: $selectedHorseId) {
                Text("None").tag(String?.none)
                ForEach(horses) { horse in
                    Text(horse.name).tag(Optional(horse.id))
                }
            }
            HStack {
                Button("Cancel", action: onCancel).foregroundColor(.red)
                Spacer()
                Button("Save",   action: onSave).foregroundColor(.blue)
            }
        }
    }
}

struct HikeUpgradeSection: View {
    @Binding var formVisible: Bool
    @Binding var selectedImage: UIImage?
    @Binding var newTitle: String
    @Binding var selectedFilters: [String]
    
    let onSubmit: ()->Void
    
    var body: some View {
        if formVisible {
            VStack {
                TextField("Title", text: $newTitle)
                Button("Select Image") { /* sheet logic */ }
                if let img = selectedImage {
                    Image(uiImage: img).resizable().scaledToFit().frame(height: 200)
                }
                FilterPicker(selectedFilters: $selectedFilters)
                Button("Submit", action: onSubmit)
                    .padding().background(Color.blue).foregroundColor(.white).cornerRadius(8)
            }
            .padding().background(Color(UIColor.systemBackground)).cornerRadius(10).shadow(radius:5)
        }
    }
}

struct HorseSelectionView: View {
    @ObservedObject var horseVM = HorseViewModel()
    var onSelect: (String) -> Void
    
    var body: some View {
        NavigationView {
            List(horseVM.horses) { horse in
                Button(action: {
                    if let horseId = horse.id {
                        onSelect(horseId)
                    }
                }) {
                    Text(horse.name)
                }
            }
            .navigationTitle("Select a Horse")
            .onAppear {
                horseVM.fetchHorses()
            }
        }
    }
}


final class MyHikesDetailsViewModel: ObservableObject {
    @Published var hike: MyHike
    @Published var coordinates: [CoordinateMyHikes] = []
    @Published var trailResponse: TrailResponse?
    @Published var selectedImage: UIImage?
    @Published var horses: [Horse] = []
    @Published var selectedFilters: [String] = []
    
    private let tokenManager = TokenManager.shared
    private var cancellables = Set<AnyCancellable>()
    private let horseVM = HorseViewModel()

    
    init(hike: MyHike) {
        self.hike = hike
    }
    
    func fetchHorses() {
        horseVM.fetchHorses()          // kick off the load…
        horseVM.$horses                // …then subscribe to its @Published
          .receive(on: DispatchQueue.main)
          .assign(to: \.horses, on: self)
          .store(in: &cancellables)
      }
    
    /// Fetch the latest hike details from server
    func fetchDetails() {
        guard let url = URL(string: "https://hopla.onrender.com/userhikes/\(hike.id)") else {
            print("❌ Invalid URL for fetchDetails")
            return
        }
        URLSession.shared.dataTaskPublisher(for: url)
            .tryMap { data, response in
                guard let http = response as? HTTPURLResponse, 200..<300 ~= http.statusCode else {
                    throw URLError(.badServerResponse)
                }
                return data
            }
            .decode(type: MyHike.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                if case let .failure(error) = completion {
                    print("❌ fetchDetails error: \(error.localizedDescription)")
                }
            }, receiveValue: { [weak self] updated in
                self?.hike = updated
            })
            .store(in: &cancellables)
    }
    
    /// Update hike details with optional image
    func updateHike(trailName: String,
                    title: String,
                    description: String,
                    horseId: String?) {
        guard let token = tokenManager.getToken() else {
            print("❌ No token found for updateHike")
            return
        }
        guard let url = URL(string: "https://hopla.onrender.com/userhikes/\(hike.id)") else {
            print("❌ Invalid URL for updateHike")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        let lineBreak = "\r\n"
        let params: [String: String] = [
            "Title": trailName,
            "Description": description,
            "TrailName": trailName
        ]
        
        // Text fields
        for (key, value) in params {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"\(key)\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append("\(value)\(lineBreak)".data(using: .utf8)!)
        }
        // HorseId if provided
        if let horse = horseId {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"HorseId\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append("\(horse)\(lineBreak)".data(using: .utf8)!)
        }
        // Image if selected
        if let image = selectedImage, let data = image.jpegData(compressionQuality: 0.8) {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"Image\"; filename=\"hike.jpg\"\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append(data)
            body.append(lineBreak.data(using: .utf8)!)
        }
        body.append("--\(boundary)--\(lineBreak)".data(using: .utf8)!)
        request.httpBody = body
        
        URLSession.shared.dataTaskPublisher(for: request)
            .tryMap { data, response in
                guard let http = response as? HTTPURLResponse, 200..<300 ~= http.statusCode else {
                    throw URLError(.badServerResponse)
                }
                return data
            }
            .decode(type: MyHike.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                if case let .failure(error) = completion {
                    print("❌ updateHike error: \(error.localizedDescription)")
                }
            }, receiveValue: { [weak self] updated in
                self?.hike = updated
            })
            .store(in: &cancellables)
    }
    
    /// Fetch coordinates or trail response
    func fetchCoordinates() {
        let endpoint = hike.trailButton
        ? "userhikes/coordinates/\(hike.id)"
        : "trails/prepare?trailId=\(hike.id)"
        guard let url = URL(string: "https://hopla.onrender.com/\(endpoint)") else {
            print("❌ Invalid URL for fetchCoordinates")
            return
        }
        URLSession.shared.dataTaskPublisher(for: url)
            .tryMap { data, response in
                guard let http = response as? HTTPURLResponse, 200..<300 ~= http.statusCode else {
                    throw URLError(.badServerResponse)
                }
                return data
            }
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { comp in
                if case let .failure(err) = comp {
                    print("❌ fetchCoordinates error: \(err)")
                }
            }, receiveValue: { [weak self] data in
                guard let self = self else { return }
                do {
                    if self.hike.trailButton {
                        let coords = try JSONDecoder().decode([CoordinateMyHikes].self, from: data)
                        self.coordinates = coords
                    } else {
                        let resp = try JSONDecoder().decode(TrailResponse.self, from: data)
                        self.trailResponse = resp
                        self.coordinates = resp.allCoords
                    }
                } catch {
                    print("❌ decode coordinates error: \(error)")
                }
            })
            .store(in: &cancellables)
    }
    
    /// Upload image and return URL string
    func uploadImage(_ image: UIImage, completion: @escaping (String?) -> Void) {
        guard let token = tokenManager.getToken(),
              let url = URL(string: "https://hopla.onrender.com/upload-image") else {
            completion(nil)
            return
        }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(image.jpegData(compressionQuality: 0.8)!)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        request.httpBody = body
        
        URLSession.shared.dataTask(with: request) { data, _, error in
            if let data = data,
               let json = try? JSONSerialization.jsonObject(with: data) as? [String: String],
               let url = json["url"] {
                DispatchQueue.main.async { completion(url) }
            } else {
                DispatchQueue.main.async { completion(nil) }
            }
        }.resume()
    }
    
    /// Upgrade hike to a trail
    func upgradeToTrail(filters: [[String: Any]], completion: @escaping (Bool) -> Void) {
        guard let token = tokenManager.getToken(),
              let url = URL(string: "https://hopla.onrender.com/trails/create/\(hike.id)") else {
            completion(false)
            return
        }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let dataJson: [String: Any] = [
            "UserHikeId": hike.id,
            "filters": filters
        ]
        guard let jsonData = try? JSONSerialization.data(withJSONObject: dataJson) else {
            completion(false)
            return
        }
        request.httpBody = jsonData
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTaskPublisher(for: request)
            .tryMap { _, response in
                guard let http = response as? HTTPURLResponse, 200..<300 ~= http.statusCode else {
                    throw URLError(.badServerResponse)
                }
                return true
            }
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { comp in
                if case .failure = comp { completion(false) }
            }, receiveValue: { success in
                completion(success)
            })
            .store(in: &cancellables)
    }
}
