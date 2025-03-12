using HoplaBackend.Models;
using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using System.Text.RegularExpressions;
using System.Text.Json;
using System.Collections.Generic;
using MediatR;
using HoplaBackend.Events;

namespace HoplaBackend.Data;

public class AppDbContext : DbContext
{
    private readonly IMediator _mediator;

    public AppDbContext(DbContextOptions<AppDbContext> options, IMediator mediator)
        : base(options)
    {
        _mediator = mediator;
    }
    //public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }
    public DbSet<SystemSetting> SystemSettings { get; set; }
    public DbSet<User> Users { get; set; }
    public DbSet<EmailVerification> EmailVerifications { get; set; }
    public DbSet<PasswordReset> PasswordResets { get; set; }
    public DbSet<UserSetting> UserSettings { get; set; }
    public DbSet<UserRelation> UserRelations { get; set; } // Endret fra Friendrequest til FriendRequest
    public DbSet<Message> Messages { get; set; }
    public DbSet<Horse> Horses { get; set; }
    public DbSet<Stable> Stables { get; set; }
    public DbSet<StableUser> StableUsers { get; set; } 
    public DbSet<StableMessage> StableMessages { get; set; }
    public DbSet<Trail> Trails { get; set; }
    public DbSet<TrailFilter> TrailFilters { get; set; }
    public DbSet<TrailDetail> TrailDetails { get; set; }
    public DbSet<TrailAllCoordinate> TrailAllCoordinates { get; set; }
    public DbSet<TrailReview> TrailReviews { get; set; }
    public DbSet<TrailRating> TrailRatings { get; set; }
    public DbSet<UserHike> UserHikes { get; set; }
    public DbSet<SubscriptionOrder> SubscriptionOrders { get; set; }
    public DbSet<Image> Images { get; set; }
    public DbSet<EntityImage> EntityImages { get; set; }
    public DbSet<EntityFeed> EntityFeeds { get; set; }
    public DbSet<EntityReaction> EntityReactions { get; set; }
    public DbSet<EntityComment> EntityComments { get; set; }
    public DbSet<UserReport> UserReports { get; set; }
    public DbSet<TrailFilterValue> TrailFilterValues { get; set; }
    public DbSet<TrailFilterDefinition> TrailFilterDefinitions { get; set; }
    public DbSet<TrailFavorite> TrailFavorites { get; set; }
    
    /*
    public DbSet<Ride> Rides { get; set; }
    public DbSet<RideDetail> RideDetails { get; set; }
    public DbSet<RideTrackingData> RideTrackingDatas { get; set; }
    public DbSet<TrackingPoint> TrackingPoints { get; set; }
    public DbSet<RideReview> RideReviews { get; set; }
    */


    public override int SaveChanges()
    {
        Console.WriteLine("SaveChanges() called");
        PublishEntityEvents().Wait();
        return base.SaveChanges();
    }

    public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        Console.WriteLine("SaveChangesAsync() called");
        await PublishEntityEvents();
        return await base.SaveChangesAsync(cancellationToken);
    }

    private async Task PublishEntityEvents()
    {
        // Forsøke å skrive om denne til å gjelde for alle Entities
        Console.WriteLine("PublishEntityEvents() started");

        var addedHorses = ChangeTracker.Entries<Horse>()
            .Where(e => e.State == EntityState.Added)
            .Select(e => e.Entity)
            .ToList();

        Console.WriteLine($"Found {addedHorses.Count} added horses");

        foreach (var horse in addedHorses)
        {
            Console.WriteLine($"Publishing event for added horse: {horse.Id}");
            await _mediator.Publish(new EntityCreatedEvent(horse.Id, "Horse", horse.UserId));
        }

        var deletedHorses = ChangeTracker.Entries<Horse>()
            .Where(e => e.State == EntityState.Deleted)
            .Select(e => e.Entity)
            .ToList();

        Console.WriteLine($"Found {deletedHorses.Count} deleted horses");

        foreach (var horse in deletedHorses)
        {
            Console.WriteLine($"Publishing event for deleted horse: {horse.Id}");
            await _mediator.Publish(new EntityDeletedEvent(horse.Id, "Horse", horse.UserId));
        }
        
        Console.WriteLine("PublishEntityEvents() completed");
    }


    protected override void OnModelCreating(ModelBuilder modelBuilder)
    
    {
        modelBuilder.HasDefaultSchema("public"); // Sikrer at EF bruker public schema
        
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<EmailVerification>()
                .HasIndex(e => new { e.Email, e.Token })
                .IsUnique();
        // Relasjon: En bruker kan ha mange hester
        // ?Dette er ikke nødvendig, da EF forstår dette automatisk, men må brukes hvis man bruker Fluent API
        modelBuilder.Entity<Horse>()
            .HasOne(h => h.User) // En hest har én eier (User)
            .WithMany(u => u.Horses) // En bruker kan ha flere hester
            .HasForeignKey(h => h.UserId) // FK ligger i Horses-tabellen
            .OnDelete(DeleteBehavior.Cascade); // Sletter hester hvis bruker slettes
        
        /*
        // 1:1 Relasjon mellom Ride og RideDetails
        modelBuilder.Entity<RideDetail>()
            .HasOne(rd => rd.Ride)
            .WithOne(r => r.RideDetails)
            .HasForeignKey<RideDetail>(rd => rd.Id)
            .OnDelete(DeleteBehavior.Cascade); // Sletter RideDetails hvis Ride slettes

        // 1:1 Relasjon mellom Ride og RideTrackingData
        modelBuilder.Entity<RideTrackingData>()
            .HasOne(rt => rt.Ride)
            .WithOne(r => r.RideTrackingDatas)
            .HasForeignKey<RideTrackingData>(rt => rt.Id)
            .OnDelete(DeleteBehavior.Cascade); // Sletter RideTrackingData hvis Ride slettes

        // 1:1 Relasjon mellom Ride og RideReview (valgfritt)
        modelBuilder.Entity<RideReview>()
            .HasOne(rr => rr.Ride)
            .WithOne(r => r.RideReviews)
            .HasForeignKey<RideReview>(rr => rr.Id)
            .IsRequired(false) // Valgfritt, kan være null
            .OnDelete(DeleteBehavior.Cascade); // Sletter review hvis Ride slettes

       
        modelBuilder.Entity<RideTrackingData>()
            .Property(r => r.TrackingPoints)
            .HasColumnType("json")  // 🚀 Bruker JSON (ikke JSONB)
            .HasConversion(
                v => JsonSerializer.Serialize(v, new JsonSerializerOptions()),  // Konverterer til JSON
                v => JsonSerializer.Deserialize<List<TrackingPoint>>(v, new JsonSerializerOptions()) ?? new List<TrackingPoint>()
            );
            */
        //
        // Trail
        //
        modelBuilder.Entity<TrailDetail>()
            .HasOne(td => td.Trail)
            .WithOne(t => t.TrailDetails)
            .HasForeignKey<TrailDetail>(td => td.Id)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<TrailAllCoordinate>()
            .HasOne(tc => tc.Trail)
            .WithOne(t => t.TrailAllCoordinates)
            .HasForeignKey<TrailAllCoordinate>(tc => tc.Id)
            .OnDelete(DeleteBehavior.Cascade);



        modelBuilder.Entity<TrailReview>()
            .HasOne(tr => tr.Trail)
            .WithMany(t => t.TrailReviews) 
            .HasForeignKey(tr => tr.TrailId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<TrailCoordinate>()
            .HasOne(tc => tc.TrailAllCoordinates)
            .WithMany(tac => tac.Coordinates)
            .HasForeignKey(tc => tc.TrailAllCoordinatesId)
            .OnDelete(DeleteBehavior.Cascade);

        // Sikrer at hvis en kommentar slettes, slettes også svarene
        modelBuilder.Entity<EntityComment>()
            .HasOne(c => c.ParentComment)
            .WithMany()
            .HasForeignKey(c => c.ParentCommentId)
            .OnDelete(DeleteBehavior.Cascade);

        // Sikrer at en brukers likes slettes når brukeren slettes
        modelBuilder.Entity<EntityReaction>()
            .HasOne(r => r.User)
            .WithMany()
            .HasForeignKey(r => r.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        //SIkrer at en bruker bare kan like ett innlegg.
        modelBuilder.Entity<EntityReaction>()
            .HasIndex(e => new { e.UserId, e.EntityId, e.EntityName })
            .IsUnique();  // Hindrer at samme bruker kan like samme ting flere ganger

    }

    

        /* Dette er nok ikke nødvendig mer etter omstruktureringen
        modelBuilder.Entity<EntityImage>()
            .HasOne(ei => ei.Image)
            .WithMany(i => i.EntityImages)
            .HasForeignKey(ei => ei.ImageId)
            .OnDelete(DeleteBehavior.Cascade); // Hvis et bilde slettes, slett koblingen

        */
        // Usikker på om denne trengs, da disse er slettet eller om indexeringen kan gjøres på en annen måte.
        //modelBuilder.Entity<EntityImage>()
        //    .HasIndex(ei => new { ei.EntityId, ei.EntityType }); // Raskere søk på koblinger

        // Spatial Index (hvis databasen støtter det)
        // Indeksering for raskere søk i databasen spesielt ved tunge data i tabellen.
        //Fluent API?
        /*
        modelBuilder.Entity<Ride>()
            .HasIndex(r => new { r.LatMean, r.LongMean }) // Vanlig indeks
            .HasDatabaseName("idx_rides_geo");

        modelBuilder.Entity<Trail>()
            .HasIndex(t => new { t.LatMean, t.LongMean }) // Vanlig indeks
            .HasDatabaseName("idx_trails_geo");

        modelBuilder.Entity<Ride>()
            .HasIndex(r => r.GeoCoordinates) // Spatial felt
            .HasDatabaseName("idx_rides_geo_spatial");
        */

       // modelBuilder.Entity<StableUser>().HasKey(su => su.Id);  //Tvinger EF til å bruke Id som primærnøkkel


        //Definere Hver eneste relasjon i hele databasen. 
        //Det er ikke nødvendig for at det skal virke, men blir kanskje mer robust og stabilt?
        //Stable-Users
        //Stable-messages
        //osv.

        /*
        foreach (var entity in modelBuilder.Model.GetEntityTypes())
        {
            entity.SetTableName(ToSnakeCase(entity.GetTableName()));

            foreach (var property in entity.GetProperties())
            {
                property.SetColumnName(ToSnakeCase(property.GetColumnName()));

                if (property.Name.EndsWith("Id"))
                {
                    property.SetColumnName(ToSnakeCase(property.Name));
                }
            }
        }
        // Mapper `FriendRequest` til riktig tabell i PostgreSQL
        modelBuilder.Entity<Friendrequest>().ToTable("friendrequest");

        // Definer primærnøkkel for FriendRequest-tabellen
        modelBuilder.Entity<Users>()
            .Property(u => u.Id)
            .HasColumnName("user_id");
        
        modelBuilder.Entity<Friendrequest>().HasKey(fr => fr.RequestId);

        base.OnModelCreating(modelBuilder);
    }
    private static string ToSnakeCase(string input)
    {
        if (string.IsNullOrEmpty(input)) return input;
        return Regex.Replace(input, "([a-z0-9])([A-Z])", "$1_$2").ToLower();
    */
}
    

