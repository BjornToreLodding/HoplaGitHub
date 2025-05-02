// MARK: - Hike Model
struct Hike: Codable, Identifiable, Equatable {
    let id: String
    var name: String
    var description: String?
    let pictureUrl: String
    let averageRating: Int
    var isFavorite: Bool
    let distance: Double?
    var latitude: Double?
    var longitude: Double?
    let filters: [HikeFilter]?

    enum CodingKeys: String, CodingKey {
        case id, name, description, pictureUrl, averageRating, isFavorite, distance, filters
        case latMean
        case longMean
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decode(String.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        description = try? container.decode(String.self, forKey: .description)
        pictureUrl = try container.decode(String.self, forKey: .pictureUrl)
        averageRating = try container.decode(Int.self, forKey: .averageRating)
        isFavorite = try container.decode(Bool.self, forKey: .isFavorite)
        distance = try? container.decode(Double.self, forKey: .distance)
        filters = try? container.decode([HikeFilter].self, forKey: .filters)

        // Decode latMean/longMean if available
        latitude = try? container.decode(Double.self, forKey: .latMean)
        longitude = try? container.decode(Double.self, forKey: .longMean)

        // If latMean/longMean are missing, attempt to decode from raw keys
        if latitude == nil || longitude == nil {
            let raw = try decoder.singleValueContainer().decode([String: AnyDecodable].self)
            if let lat = raw["latitude"]?.value as? Double {
                latitude = lat
            }
            if let lon = raw["longitude"]?.value as? Double {
                longitude = lon
            }
        }
    }
    // Custom encoding
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(name, forKey: .name)
        try container.encodeIfPresent(description, forKey: .description)
        try container.encode(pictureUrl, forKey: .pictureUrl)
        try container.encode(averageRating, forKey: .averageRating)
        try container.encode(isFavorite, forKey: .isFavorite)
        try container.encodeIfPresent(distance, forKey: .distance)
        try container.encodeIfPresent(filters, forKey: .filters)
        try container.encodeIfPresent(latitude, forKey: .latMean)
        try container.encodeIfPresent(longitude, forKey: .longMean)
    }
    init(
        id: String,
        name: String,
        description: String? = nil,
        pictureUrl: String,
        averageRating: Int,
        isFavorite: Bool,
        distance: Double? = nil,
        latitude: Double? = nil,
        longitude: Double? = nil,
        filters: [HikeFilter]? = nil
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.pictureUrl = pictureUrl
        self.averageRating = averageRating
        self.isFavorite = isFavorite
        self.distance = distance
        self.latitude = latitude
        self.longitude = longitude
        self.filters = filters
    }
}
