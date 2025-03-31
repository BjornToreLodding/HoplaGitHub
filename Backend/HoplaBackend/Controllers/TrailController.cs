using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.DTOs;
using System.IO;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using HoplaBackend.Services;
using HoplaBackend.Helpers;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using System.Text.Json;
using Microsoft.VisualBasic;
using HoplaBackend.Models.DTOs;
using System.Formats.Tar;
using System.Drawing.Drawing2D;

namespace HoplaBackend.Controllers;

[Route("trails")]
[ApiController]
public class TrailController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    private readonly TrailFavoriteService _trailFavoriteService;
    private readonly ImageUploadService _imageUploadService;
            public TrailController(Authentication authentication, AppDbContext context, TrailFavoriteService trailFavoriteService, ImageUploadService imageUploadService)
    {
        _authentication = authentication;
        _context = context;
        _trailFavoriteService = trailFavoriteService;
        _imageUploadService = imageUploadService;
    }

    [Authorize]
    [HttpGet("user")]
    public async Task<ActionResult<List<TrailDto>>> GetUserTrails(
        [FromQuery] Guid? userId, 
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        // Må skrives om når denne blir Authorized.
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }         
        if (!userId.HasValue)
        {
            userId = parsedUserId;
        }
        Console.WriteLine(userId);
        bool paging = pageNumber.HasValue && pageSize.HasValue;
        if (paging && (pageNumber < 1 || pageSize < 1))
            return BadRequest("pageNumber og pageSize må være større enn 0.");

        var trails = _context.Trails
            .Where(t => t.UserId == userId)
            .OrderByDescending(t => t.CreatedAt) // Sorterer etter nyeste innlegg
            .Select(t => new TrailDto
            {
                Id = t.Id,
                Name = t.Name,
                PictureUrl = t.PictureUrl,
                AverageRating = _context.TrailRatings
                    .Where(tr => tr.TrailId == t.Id)
                    .Select(tr => (float?)tr.Rating) // Konverterer til nullable float
                    .Average() ?? 0 // Setter 0 hvis ingen ratings finnes
            });
        if (paging)
        {
            trails = trails
                .Skip((pageNumber.Value - 1) * pageSize.Value)
                .Take(pageSize.Value);
        }
        var results = await trails.ToListAsync();

        return Ok(results);
    }

    [Authorize]
    [HttpGet("all")]
    public async Task<IActionResult> GetAllTrails(
        [FromQuery] string? search, 
        [FromQuery] string? sort, 
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10)
    {
        int page = pageNumber ?? 1;
        int size = pageSize ?? 10;

        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var query = _context.Trails.AsQueryable();

        if (!string.IsNullOrWhiteSpace(search))
        {
            query = query.Where(t => t.Name.ToLower().Contains(search.ToLower()));  
        }

        // Hent ALLE favoritt-trail IDs for brukeren i ett kall, isteden for hente for å sjekke hver tur
        var favoriteTrailIds = await _context.TrailFavorites
        .Where(tf => tf.UserId == parsedUserId)
        .Select(tf => tf.TrailId)
        .ToListAsync();

        var trails = await query
            .OrderByDescending(t => Math.Round(t.AverageRating ?? 0))
            .ThenByDescending(t => t.CreatedAt)
            .Skip((page - 1) * size)
            .Take(size)
            .ToListAsync();
        /*
        //Gammel metode, erstattet med metoden under
        var trailDtos = new List<TrailDto>();

        foreach (var trail in trails)
        {
            bool isFavorite = await _trailFavoriteService.IsTrailFavoriteAsync(parsedUserId, trail.Id);
            
            trailDtos.Add(new TrailDto
            {
                Id = trail.Id,
                Name = trail.Name,
                PictureUrl = trail.PictureUrl + "?h=140&fit=crop",
                AverageRating = trail.AverageRating ?? 0,
                IsFavorite = isFavorite
            });
        }
        */

        var trailDtos = trails.Select(trail => new TrailDto
        {
            Id = trail.Id,
            Name = trail.Name,
            PictureUrl = trail.PictureUrl + "?h=140&fit=crop",
            AverageRating = trail.AverageRating ?? 0,
            IsFavorite = favoriteTrailIds.Contains(trail.Id) // Effektiv sjekk
        }).ToList();
        var response = new 
        {
            Trails = trailDtos,
            PageNumber = page,
            PageSize = size
        };

        return Ok(response);
    }

    

    [Authorize]
    [HttpGet("list")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10,
        [FromQuery] string? filters = null, // JSON-baserte filtre
        [FromQuery] double? lengthMin = null,
        [FromQuery] double? lengthMax = null) 
    {
        Console.WriteLine("list åpnet");
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var query = _context.Trails.AsQueryable();
        Console.WriteLine(parsedUserId);
        // Hent JSON-filtre fra querystring
        if (!string.IsNullOrEmpty(filters))
        {
            try
            {
                var filterDict = JsonSerializer.Deserialize<Dictionary<string, object>>(filters);
                foreach (var filter in filterDict)
                {
                    query = query.ApplyDynamicFilter(filter.Key, filter.Value);
                }
            }
            catch (JsonException)
            {
                return BadRequest("Ugyldig JSON-format for 'filters'.");
            }
        }

        // **Filtrer ut:
        //  Mrekelige koordinater, 
        // Skjulte ruter, dvs 0 = public
        // hvis venn dvs visibulity = 1, så skal den også vises**
        
        //Ble masse trøbbel :(
        //Må fullføre dette senere når dynamiske filtere er laget. 
        // Sjekker om løype er offentlig, eller om venner only OG at man er venn med personen som har laget løypa.
        query = query.Where(t => t.LatMean != 0 && t.LongMean != 0 && t.Visibility == 0); // && (t.Visibility == 1 && t.UserId == "FRIENDS"));

        // **Filtrer etter lengde før vi henter ut data fra databasen**
        query = query.Where(t => (!lengthMin.HasValue || t.Distance >= lengthMin) &&  
                                (!lengthMax.HasValue || t.Distance <= lengthMax));
            // Hent dataene fra databasen først (uten sortering på distanse)

        var favoriteTrailIds = await _context.TrailFavorites
        .Where(tf => tf.UserId == parsedUserId)
        .Select(tf => tf.TrailId)
        .ToListAsync();

        var trailList = await query
        .Select(t => new
        {
            t.Id,
            t.Name,
            t.LatMean,
            t.LongMean,
            t.PictureUrl,
            t.AverageRating,
            t.Distance
        })
        .ToListAsync(); // 🚀 Flytter dataene til minnet
        Console.WriteLine("Traillist");
                Console.WriteLine(trailList);
        // 🚀 Nå kan vi bruke `DistanceCalc.SimplePytagoras()` i minnet
        var sortedTrails = trailList
        .Select(t => new
        {
            t.Id,
            t.Name,
            Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.LatMean, t.LongMean),
            t.AverageRating,
            t.PictureUrl
        })
        .OrderBy(t => t.Distance) // Nå fungerer det siden vi er i minnet
        .Skip(((pageNumber ?? 1) - 1) * (pageSize ?? 10))
        .Take(pageSize ?? 10)
        .ToList(); // Utfør paginering i minnet

        var response = new 
        {
            Trails = sortedTrails.Select(t => new
            {
                t.Id,
                t.Name,
                t.Distance,
                IsFavorite = favoriteTrailIds.Contains(t.Id),
                t.AverageRating,
                t.PictureUrl
            }),
            PageNumber = pageNumber,
            PageSize = pageSize
        };
                Console.WriteLine(response);
        return Ok(response);

    }
    [Authorize]
    [HttpGet("favorites")]
    public async Task<IActionResult> GetFavoriteTrails(
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10,
        [FromQuery] string? filters = null, // JSON-baserte filtre
        [FromQuery] double? lengthMin = null,
        [FromQuery] double? lengthMax = null) 
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var query = _context.Trails.AsQueryable();

        var favoriteTrailIds = await _context.TrailFavorites
        .Where(tf => tf.UserId == parsedUserId)
        .Select(tf => tf.TrailId)
        .ToListAsync();


        var trailList = await query
        .Select(t => new
        {
            t.Id,
            t.Name,
            t.PictureUrl,
            t.AverageRating,
        })
        .ToListAsync(); // 🚀 Flytter dataene til minnet

        var sortedTrails = trailList
        .Select(t => new
        {
            t.Id,
            t.Name,
            t.AverageRating,
            t.PictureUrl,
            IsFavorite = favoriteTrailIds.Contains(t.Id)
        })
        .Where(t => t.IsFavorite)
        .Skip(((pageNumber ?? 1) - 1) * (pageSize ?? 10))
        .Take(pageSize ?? 10)
        .ToList(); // Utfør paginering i minnet

        var response = new 
        {
            Trails = sortedTrails.Select(t => new
            {
                t.Id,
                t.Name,
                IsFavorite = favoriteTrailIds.Contains(t.Id),
                t.AverageRating,
                t.PictureUrl
            }),
            PageNumber = pageNumber,
            PageSize = pageSize
        };
        Console.WriteLine(response);
        return Ok(response);

    }

    [Authorize]
    [HttpGet("relations")]
    public async Task<IActionResult> GetFavoriteTrails(
        [FromQuery] bool? following = false, 
        [FromQuery] bool? friends = false,
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10,
        [FromQuery] string? filters = null, // JSON-baserte filtre
        [FromQuery] double? distMin = null,
        [FromQuery] double? distMax = null) 
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        // Liste for å samle relevante bruker-ID-er
        List<Guid> relevantUserIds = new();

        // Hvis `following == true`, legg til ID-ene til de brukeren følger
        if (following == true)
        {
            var followedUserIds = await _context.UserRelations
                .Where(ur => ur.FromUserId == parsedUserId && ur.Status == "FOLLOWING")
                .Select(ur => ur.ToUserId)
                .ToListAsync();

            relevantUserIds.AddRange(followedUserIds);
        }

        // Hvis `friends == true`, legg til ID-ene til vennene
        if (friends == true)
        {
            var friendUserIds = await _context.UserRelations
                .Where(ur => (ur.FromUserId == parsedUserId || ur.ToUserId == parsedUserId) && ur.Status == "FRIENDS")
                .Select(ur => ur.FromUserId == parsedUserId ? ur.ToUserId : ur.FromUserId)
                .ToListAsync();

            relevantUserIds.AddRange(friendUserIds);
        }

        // Fjern duplikater
        relevantUserIds = relevantUserIds.Distinct().ToList();

        // Hent favorittløypene til venner/følgere
        var favoriteTrailIds = await _context.TrailFavorites
            .Where(tf => relevantUserIds.Contains(tf.UserId))
            .Select(tf => tf.TrailId)
            .Distinct()
            .ToListAsync();

        // Hent favorittløypene til den innloggede brukeren
        var userFavoriteTrailIds = await _context.TrailFavorites
            .Where(tf => tf.UserId == parsedUserId)
            .Select(tf => tf.TrailId)
            .ToListAsync(); //teste denne først.
            //.ToHashSetAsync(); // Bruker HashSet for raskere oppslag


        // Filtrer trailene basert på favoritter fra venner/følgere
        var query = _context.Trails
            .Where(t => favoriteTrailIds.Contains(t.Id));

        /*
        if (lengthMin.HasValue)
        {
            query = query.Where(t => t.Length >= lengthMin.Value);
        }

        if (lengthMax.HasValue)
        {
            query = query.Where(t => t.Length <= lengthMax.Value);
        }
        */

        // Paginering og seleksjon
        var trails = await query
            .Select(t => new
            {
                t.Id,
                t.Name,
                t.PictureUrl,
                t.AverageRating,
                IsFavorite = userFavoriteTrailIds.Contains(t.Id) // Sjekker om den innloggede brukeren har denne løypa som favoritt
            })
            .OrderByDescending(t => t.AverageRating) // Sortering
            .ThenByDescending(t => t.Name)
            .Skip(((pageNumber ?? 1) - 1) * (pageSize ?? 10))
            .Take(pageSize ?? 10)
            .ToListAsync();

        return Ok(new 
        {
            Trails = trails.Select(t => new
            {
                t.Id,
                t.Name,
                t.IsFavorite,
                t.AverageRating,
                t.PictureUrl
            }),
            PageNumber = pageNumber,
            PageSize = pageSize
        });
    }

    //[Authorize]
    [HttpGet("map")]
    public async Task<IActionResult> CreateTrailsList(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] double? Width ,
        [FromQuery] double? Height,
        [FromQuery] int zoomlevel) //
    {
        //Width og Height er optional. Hvis de ikke er oppgitt, settes de til følgende standardverdier
        double screenPixelsWidth = Width ?? 1080;
        double screenPixelsHeight = Height ?? 2400;

        // beregne hvilke verdier skjermen har ut i fra zoomlevel. 
        var (latMin, latMax, longMin, longMax) = CoordinateCalculator.MapZoomLevel(latitude, longitude, screenPixelsWidth, screenPixelsHeight, zoomlevel);

        // Henter trails direkte fra databasen uten referanser til Rides
        //var trails = await _context.Trails.ToListAsync();

        //Legg til så vises kun hvis offentlig eller kun for venner og relationstatus=FRIENDS
        
        Console.ForegroundColor = ConsoleColor.Blue; // Endrer tekstfarge til lilla
        Console.WriteLine($"latitude {latitude}");
        Console.WriteLine($"longitude {longitude}");
        Console.WriteLine($"latMin {latMin}");
        Console.WriteLine($"latMax {latMax}");
        Console.WriteLine($"longMin {longMin}");
        Console.WriteLine($"longMax {longMax}");
        Console.ResetColor();
        List<object> validTrails = new List<object>();
        
        // Finne ruter som kan tegnes inn på kartet.
        // Dette gjøres på en veldig enkel måte, men medfører at den også finner noen ruter utenfor kartet.
        // Dette er til hjelp når man skal flytte kartutsnittet.
        // Denne metoden medfører minimalt med regneoperasjoner som gir bedre responstid
        // Planen er også caching av det som allerede er tegnet opp, slik at man slipper å hente samme data igjen og igjen.
        //
        // Finne alle ruter som kan touche kartet ved å bruke latMean og longMean og t.distance / 2
        // Dette kan kun skje hvis ruta går på perfekt rett linje enten vertikalt eller horisontalt.
        // Så benyttes enkle kollisjonsdeteksjons-teknikker. 
        //først sjekke mot latitude og om løypa kan ligge innenfor kartutsnittet. 
        // Det gjør ingen ting å ta det med hvis det ligger litt utenfor. 
        // Det er bedre å tegne opp lit for mye som ligger uten for kartutsnittet enn at backend skal gjøre unødvendig mye beregninger
    
        // Filtrerer trails som er innenfor kartgrensene

        var trails = await _context.Trails
            
            .Where(t => t.LatMean >= latMin && t.LatMean <= latMax 
                    && t.LongMean >= longMin && t.LongMean <= longMax)
            .Select(t => new
            {
                t.Id,
                t.Name,
                // Avhengig av zoomnivå, så returneres forskjellige nivåer.
                t.LatMean,
                t.LongMean,
                t.TrailAllCoordinates
            })
            .ToListAsync();

        return Ok(trails);   
    }


    [Authorize]
    [HttpPost("rate")]
    public async Task<IActionResult> CreateRating([FromBody] TrailRateDto request)
    {
        if (request.Rating < 1 || request.Rating > 5) return BadRequest("Ratingen må være mellom 1 og 5");
        var userId = _authentication.GetUserIdFromToken(User);
        
        var existing = await _context.TrailRatings.FirstOrDefaultAsync(
            tr => tr.UserId == userId && tr.TrailId == request.TrailId);
        if (existing != null) return await UpdateRating(existing.Id, request.Rating);
        
        var rating = new TrailRating
        {
            UserId = userId,
            TrailId = request.TrailId,
            Rating = request.Rating
        };
        _context.TrailRatings.Add(rating);
        await _context.SaveChangesAsync();

        return Ok("Trail Rated");
    }

    //I tilfelle en bruker har ratet en trail fra før, så brukes denne funksjonen for å oppdatere ratingen.
    public async Task<IActionResult> UpdateRating(Guid trailRatingId, int rating)
    {
        var existing = await _context.TrailRatings.FirstOrDefaultAsync(tr => tr.Id == trailRatingId);
        if (existing == null) return NotFound("Rating Not Found");
        
        existing.Rating = rating;
        await _context.SaveChangesAsync();
        return Ok(new {message = "Updated TrailRating"}); //, rating = existing});
    }
    [Authorize]
    [HttpGet("updates")]
    public async Task<IActionResult> GetTrailReviews([FromQuery] Guid trailId, [FromQuery] Guid? trailReviewId, [FromQuery] int? pageSize = 10, [FromQuery] int? pageNumber = 1)
    {
        var query = _context.TrailReviews
            .Where(r => r.TrailId == trailId);

        if (trailReviewId.HasValue)
        {
            query = query.Where(r => r.Id == trailReviewId.Value);
        }

        var reviews = await query
            .OrderByDescending(r => r.CreatedAt)
            .Select(r => new TrailReviewResponseDto
            {
                Id = r.Id,
                Comment = r.Comment,
                PictureUrl =  !string.IsNullOrEmpty(r.PictureUrl)  ? (r.PictureUrl.Contains("http") ? r.PictureUrl : "https://hopla.imgix.net/" + r.PictureUrl) + "?w=200&h=200&fit=crop" : "",
                Condition = r.Condition,
                CreatedAt = r.CreatedAt,
                Alias = r.User.Alias
            })
            .Skip(((pageNumber ?? 1) - 1) * (pageSize ?? 10))
            .Take(pageSize ?? 10)
            .ToListAsync();

        return Ok(reviews);
    }


    [Authorize]
    [HttpPost("review")]
    public async Task<IActionResult> CreateReview([FromForm] TrailReviewForm dto)
    {
        string? image = null;

        if (dto.Image != null)
        {
            // Bruk din ImageUploadService eller tilsvarende her
            image = await _imageUploadService.UploadImageAsync(dto.Image);
        }

        var review = new TrailReview
        {
            Id = Guid.NewGuid(),
            TrailId = dto.TrailId,
            UserId = _authentication.GetUserIdFromToken(User),
            Comment = dto.Message,
            PictureUrl = image,
            Condition = dto.Condition,
            CreatedAt = DateTime.UtcNow
        };

        _context.TrailReviews.Add(review);
        await _context.SaveChangesAsync();

        return Ok("Review opprettet");
            }

    [HttpGet("prepare")]
    public async Task<IActionResult> StartTrail([FromQuery] Guid trailId)
    {
        var trailData = await _context.Trails.FirstOrDefaultAsync(t => t.Id == trailId);
        if (trailData == null) 
        {
            return BadRequest("Trail finnes ikke");
        }
        //Bare fiktiv for å generere løypa
        var allCoords = MockHelper.GenerateCircularTrail(trailData.LatMean, trailData.LongMean, trailData.Distance);
        var response = new
        {
            trailData.Id,
            trailData.Distance,
            allCoords
        };

        return Ok(response);
    }
    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateTrail([FromBody] CreateTrailDto dto)
    {
        Console.WriteLine("trails create start");
        var userId = _authentication.GetUserIdFromToken(User);

        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        var hike = await _context.UserHikes.FirstOrDefaultAsync(h => h.Id == dto.UserHikeId);
        var hikeDetails = await _context.UserHikeDetails.FirstOrDefaultAsync(hd => hd.UserHikeId == dto.UserHikeId);

        if (hike == null || hikeDetails == null)
            return BadRequest("Tur eller detaljer ikke funnet");

        var trail = new Trail
        {
            Id = Guid.NewGuid(),
            Name = dto.Name,
            LatMean = hikeDetails.LatMean,
            LongMean = hikeDetails.LongMean,
            Distance = hike.Distance,
            PictureUrl = dto.PictureUrl,
            Visibility = TrailVisibility.Public,
            UserId = userId,
        };

        // Hent koordinater og lag 50 punkter
        var parsed = CoordinateHelper.ParseLatLngOnly(hikeDetails.CoordinatesCsv);
        var reduced = CoordinateHelper.DownsampleCoordinates(parsed, 50);
        var reducedCsv = CoordinateHelper.ToCsv(reduced);

        var trailDetails = new TrailDetail
        {
            Description = hikeDetails.Description,
            LatMin = hikeDetails.LatMin,
            LatMax = hikeDetails.LatMax,
            LongMin = hikeDetails.LongMin,
            LongMax = hikeDetails.LongMax,
            PreviewCoordinatesCsv = reducedCsv //50koordinater
        };

        var trailAllCoordinates = new TrailAllCoordinate
        {
            CoordinatesCsv = hikeDetails.CoordinatesCsv // full csv med offset beholdes
        };

        _context.Trails.Add(trail);
        _context.TrailDetails.Add(trailDetails);
        _context.TrailAllCoordinates.Add(trailAllCoordinates);

        // Legg til TrailFilterValues
        foreach (var filter in dto.Filters)
        {
            var definitionExists = await _context.TrailFilterDefinitions
                .AnyAsync(d => d.Id == filter.FilterDefinitionId);

            if (!definitionExists)
                continue; // hopp over ukjente filterDefinisjoner

            var value = new TrailFilterValue
            {
                Id = Guid.NewGuid(),
                TrailId = trail.Id,
                FilterDefinitionId = filter.FilterDefinitionId,
                Value = filter.Value
            };

            _context.TrailFilterValues.Add(value);
        }
        await _context.SaveChangesAsync();

        return Ok(new { trail.Id, Message = "Trail created" });
    }

/*
    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateTrail([FromBody] CreateTrailDto dto)
    {
        Console.WriteLine("trails create start");
         var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });


        var hike = await _context.UserHikes.FirstOrDefaultAsync(h => h.Id == dto.UserHikeId);
        var hikeDetails = await _context.UserHikeDetails.FirstOrDefaultAsync(hd => hd.UserHikeId == dto.UserHikeId);
        var trail = new Trail
        {
            Id = Guid.NewGuid(),

            Name = dto.Name,
            LatMean = hikeDetails.LatMean, //dto.LatMean,
            LongMean = hikeDetails.LongMean, //10, //dto.LongMean,
            Distance = hike.Distance,
            PictureUrl = dto.PictureUrl,
            Visibility = TrailVisibility.Public,
            UserId = userId, // evt. UserId fra innlogget bruker?
        };

        // Lag liste med 50 coordinater som cvs-format.
        var coordinates = hikeDetails.CoordinatesCsv;
        var coordinates50 = hikeDetails.CoordinatesCsv;
        var trailDetails = new TrailDetail
        {
            Description = hikeDetails.Description,
            LatMin = hikeDetails.LatMin,
            LatMax = hikeDetails.LatMax,
            LongMin = hikeDetails.LongMin,
            LongMax = hikeDetails.LongMax,
            PreviewCoordinatesCsv = coordinates50, //Rødt strek under reducedCoords
            
        };

        var trailAllCoordinates = new TrailAllCoordinate
        {
            CoordinatesCsv = coordinates
        };

        _context.Trails.Add(trail);
        _context.TrailDetails.Add(trailDetails);
        _context.TrailAllCoordinates.Add(trailAllCoordinates);
        await _context.SaveChangesAsync();

        return Ok(new { trail.Id, Message = "Trail created" });
    }
*/
    [Authorize]
    [HttpPost("favorite")]
    public async Task<IActionResult> CreateTrailFavorite([FromBody] TrailFavoriteDto dto)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // Sjekk at løypa finnes
        var trailExists = await _context.Trails.AnyAsync(t => t.Id == dto.TrailId);
        if (!trailExists)
            return NotFound(new { message = "Løype ikke funnet" });

        // Sjekk om favoritt allerede finnes
        var existing = await _context.TrailFavorites.FirstOrDefaultAsync(tf =>
            tf.UserId == userId && tf.TrailId == dto.TrailId);

        if (existing != null)
            return BadRequest(new { message = "Løype er allerede lagt til som favoritt" });

        // Opprett ny favoritt
        var trailFavorite = new TrailFavorite
        {
            UserId = userId,
            TrailId = dto.TrailId
        };

        _context.TrailFavorites.Add(trailFavorite);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Favoritt lagt til" });
    }

    [Authorize]
    [HttpDelete("favorite")]
    public async Task<IActionResult> RemoveTrailFavorite([FromBody] TrailFavoriteDto dto)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // Sjekk at løypa finnes
        var trailExists = await _context.Trails.AnyAsync(t => t.Id == dto.TrailId);
        if (!trailExists)
            return NotFound(new { message = "Løype ikke funnet" });

        // Finn favoritten
        var trailFavorite = await _context.TrailFavorites.FirstOrDefaultAsync(tf =>
            tf.UserId == userId && tf.TrailId == dto.TrailId);

        if (trailFavorite == null)
            return NotFound(new { message = "Favoritt ikke funnet" });

        _context.TrailFavorites.Remove(trailFavorite);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Favoritt fjernet" });
    }

    [HttpPost("mock")]
    public async Task<IActionResult> CreateMockTrail([FromBody] CreateMockTrailDto dto)
    {
        Console.WriteLine("trails/mock start");

        // Generer full liste med koordinater
        var allCoords = MockHelper.GenerateCircularTrail(dto.LatMean, dto.LongMean, dto.Distance);

        // Reduser til 50 for preview
        var reducedCoords = CoordinateHelper.DownsampleCoordinates(allCoords, 50);

        // Statistikk
        var latMin = allCoords.Min(c => c.Lat);
        var latMax = allCoords.Max(c => c.Lat);
        var longMin = allCoords.Min(c => c.Lng);
        var longMax = allCoords.Max(c => c.Lng);

        var trail = new Trail
        {
            Id = Guid.NewGuid(),
            Name = dto.Name,
            LatMean = dto.LatMean,
            LongMean = dto.LongMean,
            Distance = dto.Distance,
            PictureUrl = null,
            Visibility = TrailVisibility.Private,
            UserId = null,
            TrailDetails = new TrailDetail
            {
                Description = "Auto-generert mock-løype",
                LatMin = latMin,
                LatMax = latMax,
                LongMin = longMin,
                LongMax = longMax,
                PreviewCoordinatesCsv = CoordinateHelper.ToCsv(reducedCoords)
            },
            TrailAllCoordinates = new TrailAllCoordinate
            {
                CoordinatesCsv = CoordinateHelper.ToCsv(allCoords)
            }
        };

        _context.Trails.Add(trail);
        await _context.SaveChangesAsync();

        return Ok(new { trail.Id, Message = "Mock trail created" });
    }

    /*
    [HttpPost("mock")]
    public async Task<IActionResult> CreateMockTrail([FromBody] CreateMockTrailDto dto)
    {
        Console.WriteLine("trails/mock start");

        // Genererer full liste med koordinater
        var allCoords = MockHelper.GenerateCircularTrail(dto.LatMean, dto.LongMean, dto.Distance);

        // Reduserer til 50 punkter for forhåndsvisning
        var reducedCoords = TrailCoordinatesTrim.ReduceTo50Coordinates(
            allCoords.Select(c => new TrailCoordinateDto { Lat = c.Lat, Long = c.Long }).ToList()
        );

        // Utregner statistikk
        var latMin = allCoords.Min(c => c.Lat);
        var latMax = allCoords.Max(c => c.Lat);
        var longMin = allCoords.Min(c => c.Long);
        var longMax = allCoords.Max(c => c.Long);

        // Lager mock-trail
        var trail = new Trail
        {
            Id = Guid.NewGuid(),
            Name = dto.Name,
            LatMean = dto.LatMean,
            LongMean = dto.LongMean,
            Distance = dto.Distance,
            PictureUrl = null,
            Visibility = TrailVisibility.Private,
            UserId = null,
            TrailDetails = new TrailDetail
            {
                Description = "Auto-generert mock-løype",
                LatMin = latMin,
                LatMax = latMax,
                LongMin = longMin,
                LongMax = longMax,
                // Bred modell for preview-koordinater
                PreviewCoordinatesCsv = string.Join(";", reducedCoords.Select(c => $"{c.Lat},{c.Long}"))
            },
            TrailAllCoordinates = new TrailAllCoordinate
            {
                // Bred modell for alle koordinater
                CoordinatesCsv = string.Join(";", allCoords.Select(c => $"{c.Lat},{c.Long}"))
            }
        };

        _context.Trails.Add(trail);
        await _context.SaveChangesAsync();

        return Ok(new { trail.Id, Message = "Mock trail created" });
    }
    */

}

/*
    //Gammel høy modell
    [HttpPost("mock")]
    public async Task<IActionResult> CreateMockTrail([FromBody] CreateMockTrailDto dto)
    {
        Console.WriteLine("trails/mock start");
        var allCoords = MockHelper.GenerateCircularTrail(dto.LatMean, dto.LongMean, dto.Distance);
        var reducedCoords = TrailCoordinatesTrim.ReduceTo50Coordinates(allCoords.Select(c => new TrailCoordinateDto { Lat = c.Lat, Long = c.Long }).ToList());

        var convertedCoords = reducedCoords
            .Select(c => new TrailCoordinate50 { Lat = c.Lat, Long = c.Long })
            .ToList();
        var latMin = allCoords.Min(c => c.Lat);
        var latMax = allCoords.Max(c => c.Lat);
        var longMin = allCoords.Min(c => c.Long);
        var longMax = allCoords.Max(c => c.Long);
        Console.WriteLine(latMin);
        var trail = new Trail
        {
            Id = Guid.NewGuid(),
            Name = dto.Name,
            LatMean = dto.LatMean,
            LongMean = dto.LongMean,
            Distance = dto.Distance,
            PictureUrl = null,
            Visibility = TrailVisibility.Private,
            UserId = null, // evt. UserId fra innlogget bruker?
            TrailDetails = new TrailDetail
            {
                Description = "Auto-generert mock-løype",
                Coordinates50 = convertedCoords, //Rødt strek under reducedCoords
                LatMin = latMin,
                LatMax = latMax,
                LongMin = longMin,
                LongMax = longMax,
            },
            TrailAllCoordinates = new TrailAllCoordinate
            {
                Coordinates = allCoords
                    .Select(c => new TrailAllCoordinate
                    {
                        Lat = c.Lat,
                        Lng = c.Long
                    }).ToList()
            }
        };

        _context.Trails.Add(trail);
        await _context.SaveChangesAsync();

        return Ok(new { trail.Id, Message = "Mock trail created" });
    }
}
    
    [HttpGet("list")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] int? pageNumber, 
        [FromQuery] int? pageSize,
        [FromQuery] bool filter = false, // Hvis "filter" er false, returner alle trails
        //Omskriving av filter
        [FromQuery] string? difficulty = null,
        [FromQuery] bool? riverBridge = null,
        [FromQuery] bool? cart = null,
        [FromQuery] bool? roadCrossing = null,
        [FromQuery] bool? traficRoads = null,
        [FromQuery] bool? peopleTraffic = null,
        [FromQuery] double? lengthMin = null,
        [FromQuery] double? lengthMax = null)
    {
        // Start med alle trails
        var query = _context.Trails.AsQueryable();

        if (filter)
        {
            //if (!string.IsNullOrEmpty(difficulty))
            //    query = query.Where(t => t.Difficulty == difficulty);
                // Length { get; set; }
                // HasBridge { get; set; }
                // Season { get; set; } = string.Empty; // F.eks. "Summer, Winter"
                // Cart { get; set; } // Kan man bruke hest og vogn?
                // TrafficRoads { get; set; } // Går den langs bilvei?
                // PeopleTraffic { get; set; } // Mye folk?
                // Other { get; set; } = string.Empty; // Annen info
            if (cart.HasValue)
                query = query.Where(t => t.TrailFilters.Cart == cart.Value);

            //if (roadCrossing.HasValue)
            //    query = query.Where(t => t.RoadCrossing == roadCrossing.Value);

            if (traficRoads.HasValue)
                query = query.Where(t => t.TrailFilters.TrafficRoads == traficRoads.Value);

            if (peopleTraffic.HasValue)
                query = query.Where(t => t.TrailFilters.PeopleTraffic == peopleTraffic.Value);

            if (lengthMin.HasValue)
                query = query.Where(t => t.TrailFilters.Length >= lengthMin.Value);

            if (lengthMax.HasValue)
                query = query.Where(t => t.TrailFilters.Length <= lengthMax.Value);
        }

        
        // Henter trails direkte fra databasen uten referanser til Rides
        //var trails = await _context.Trails.ToListAsync();
        var trails = await query.ToListAsync(); // Bruker queryet med filtrene

        List<object> validTrails = new List<object>();
        int excludedCount = 0;
        string logFilePath = "logs/trail_errors.log";
        string? logDirectory = Path.GetDirectoryName(logFilePath);

        if (!string.IsNullOrEmpty(logDirectory))
        {
            Directory.CreateDirectory(logDirectory);
        }

        using (StreamWriter logFile = new StreamWriter(logFilePath, true))
        {
            foreach (var t in trails)
            {
                //if (!t.LatMean.Value || !t.LongMean.HasValue) //fungerer ikke. Nødløsning nedenfor
                if (t.LatMean == 0 || t.LongMean == 0)

                {
                    excludedCount++;
                    string errorMessage = $"Trail '{t.Name}' (ID: {t.Id}) har ugyldige koordinater.";

                    Console.WriteLine("Warning: " + errorMessage);
                    logFile.WriteLine($"{DateTime.UtcNow}: {errorMessage}");
                    continue;
                }

                validTrails.Add(new
                {
                    t.Id,
                    t.Name,
                    //t.TrailDetails.Description, 
                    Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.LatMean, t.LongMean)
                });
            }
        }

        Console.WriteLine($"Antall trails filtrert ut pga. manglende koordinater: {excludedCount}");
        Console.WriteLine($"Antall gyldige trails som sendes til frontend: {validTrails.Count}");

        var sortedTrails = validTrails.OrderBy(t => ((dynamic)t).Distance).ToList();
        return Ok(sortedTrails);
    }
*()
    [HttpGet("map")]
    public async Task<IActionResult> CreateTrailsList(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] double? Width ,
        [FromQuery] double? Height,
        [FromQuery] int zoomlevel) //
    {
        //Width og Height er optional. Hvis de ikke er oppgitt, settes de til følgende standardverdier
        double screenPixelsWidth = Width ?? 1080;
        double screenPixelsHeight = Height ?? 2400;

        // beregne hvilke verdier skjermen har ut i fra zoomlevel. 
        var (latMin, latMax, longMin, longMax) = CoordinateCalculator.MapZoomLevel(latitude, longitude, screenPixelsWidth, screenPixelsHeight, zoomlevel);

        // Henter trails direkte fra databasen uten referanser til Rides
        //var trails = await _context.Trails.ToListAsync();

        
        Console.ForegroundColor = ConsoleColor.Blue; // Endrer tekstfarge til lilla
        Console.WriteLine($"latMin {latMin}");
        Console.WriteLine($"latMax {latMax}");
        Console.WriteLine($"longMin {longMin}");
        Console.WriteLine($"longMax {longMax}");
        Console.ResetColor();
        List<object> validTrails = new List<object>();
        
        // Finne ruter som kan tegnes inn på kartet.
        // Dette gjøres på en veldig enkel måte, men medfører at den også finner noen ruter utenfor kartet.
        // Dette er til hjelp når man skal flytte kartutsnittet.
        // Denne metoden medfører minimalt med regneoperasjoner som gir bedre responstid
        // Planen er også caching av det som allerede er tegnet opp, slik at man slipper å hente samme data igjen og igjen.
        //
        // Finne alle ruter som kan touche kartet ved å bruke latMean og longMean og t.distance / 2
        // Dette kan kun skje hvis ruta går på perfekt rett linje enten vertikalt eller horisontalt.
        // Så benyttes enkle kollisjonsdeteksjons-teknikker. 
        //først sjekke mot latitude og om løypa kan ligge innenfor kartutsnittet. 
        // Det gjør ingen ting å ta det med hvis det ligger litt utenfor. 
        // Det er bedre å tegne opp lit for mye som ligger uten for kartutsnittet enn at backend skal gjøre unødvendig mye beregninger
    
        // Filtrerer trails som er innenfor kartgrensene
        var trails = await _context.Trails
            .Where(t => t.LatMean >= latMin && t.LatMean <= latMax 
                    && t.LongMean >= longMin && t.LongMean <= longMax)
            .ToListAsync();

        return Ok(trails);   
         }

    /*[HttpGet("closest")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude)
    {
        //Må skrives om pga omstrukturering av database.
        var trails = await _context.Trails
            .Include(t => t.Ride)           // Hvis Ride fortsatt finnes
            .Include(t => t.Ride.RideDetails)    // Legger til RideDetail
            .ToListAsync();

        //Console.WriteLine($"Antall trails hentet fra databasen: {trails.Count}");

        List<object> validTrails = new List<object>();
        int excludedCount = 0;
        string logFilePath = "logs/trail_errors.log";
        string? logDirectory = Path.GetDirectoryName(logFilePath);

        if (!string.IsNullOrEmpty(logDirectory))
        {
            Directory.CreateDirectory(logDirectory);
        }

        using (StreamWriter logFile = new StreamWriter(logFilePath, true))
        {
            foreach (var t in trails)
            {
                if (t.Ride == null || !t.Ride.RideDetails.LatMean.HasValue || !t.Ride.RideDetails.LatMean.HasValue)
                {
                    excludedCount++;
                    string errorMessage = $"Trail '{t.Name}' (ID: {t.Id}) har ugyldige koordinater eller mangler RideData.";

                    Console.WriteLine("Warning: " + errorMessage);
                    logFile.WriteLine($"{DateTime.UtcNow}: {errorMessage}");
                    continue;
                }

                validTrails.Add(new
                {
                    t.Id,
                    t.Name,
                    t.TrailDetails.Description, 
                    Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.Ride.RideDetails.LatMean.Value, t.Ride.RideDetails.LatMean.Value)
                });
            }
        }

        Console.WriteLine($"Antall trails filtrert ut pga. manglende RideData/koordinater: {excludedCount}");
        Console.WriteLine($"Antall gyldige trails som sendes til frontend: {validTrails.Count}");

        var sortedTrails = validTrails.OrderBy(t => ((dynamic)t).Distance).ToList();
        return Ok(sortedTrails);
    }
    */


    /*
    [HttpGet("closest")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude)
    {
        // Hent dataene fra databasen, men gjør IKKE beregninger i SQL
        var trails = await _context.Trails
            .Include(t => t.Ride)
            .Where(t => t.Ride != null && t.Ride.LatMean.HasValue && t.Ride.LongMean.HasValue) // Fjerner null-verdier
            .ToListAsync(); // Flytter dataene til minnet (C#)

        // Beregn avstand i minnet med LINQ
        var sortedTrails = trails
            .Select(t => new
            {
                t.Id,
                t.Name,
                t.Beskrivelse,
                //RideId = t.RideId,
                Distance = GetDistance(latitude, longitude, t.Ride.LatMean.Value, t.Ride.LongMean.Value) // Nå fungerer det
            })
            .OrderBy(t => t.Distance) // Sortering skjer i minnet, ikke i SQL
            .ToList();

        return Ok(sortedTrails);
    }
    */


    // Erstattet med enklere formel.
    // Beregner avstand mellom to koordinater med Haversine-formelen. Ikke i bruk lenger.

    /*
    private static double GetDistance(double lat1, double lon1, double lat2, double lon2)
    {
        var R = 6371.0; // Jordens radius i km
        var dLat = (lat2 - lat1) * Math.PI / 180.0;
        var dLon = (lon2 - lon1) * Math.PI / 180.0;
        var a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                Math.Cos(lat1 * Math.PI / 180.0) * Math.Cos(lat2 * Math.PI / 180.0) *
                Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
        var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
        return R * c; // Avstand i km
    }
    */

