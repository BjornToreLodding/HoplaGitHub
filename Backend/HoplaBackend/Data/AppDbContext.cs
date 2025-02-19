using HoplaBackend.Models;
using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using System.Text.RegularExpressions;
using System.Text.Json;
using System.Collections.Generic;

namespace HoplaBackend.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<User> Users { get; set; }
    public DbSet<UserRelation> UserRelations { get; set; } // Endret fra Friendrequest til FriendRequest
    public DbSet<Message> Messages { get; set; }
    public DbSet<Horse> Horses { get; set; }
    public DbSet<Ride> Rides { get; set; }
    public DbSet<RideDetail> RideDetails { get; set; }
    public DbSet<RideTrackingData> RideTrackingDatas { get; set; }
    public DbSet<TrackingPoint> TrackingPoints { get; set; }
    public DbSet<RideReview> RideReviews { get; set; }
    public DbSet<Stable> Stables { get; set; }
    public DbSet<StableUser> StableUsers { get; set; } 
    public DbSet<StableMessage> StableMessages { get; set; }
    public DbSet<Trail> Trails { get; set; }
    public DbSet<TrailFilter> TrailFilters { get; set; }
    public DbSet<TrailDetail> TrailDetails { get; set; }
    public DbSet<TrailAllCoordinate> TrailAllCoordinates { get; set; }
    public DbSet<Image> Images { get; set; }
    public DbSet<EntityImage> EntityImages { get; set; }



    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        // Relasjon: En bruker kan ha mange hester
        // ?Dette er ikke nødvendig, da EF forstår dette automatisk, men må brukes hvis man bruker Fluent API
        modelBuilder.Entity<Horse>()
            .HasOne(h => h.User) // En hest har én eier (User)
            .WithMany(u => u.Horses) // En bruker kan ha flere hester
            .HasForeignKey(h => h.UserId) // FK ligger i Horses-tabellen
            .OnDelete(DeleteBehavior.Cascade); // Sletter hester hvis bruker slettes
        
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

        // 1:M Relasjon mellom RideDetails og EntityImages
        modelBuilder.Entity<EntityImage>()
            .HasOne(ei => ei.RideDetails)
            .WithMany(rd => rd.Images)
            .HasForeignKey(ei => ei.RideDetailId)
            .OnDelete(DeleteBehavior.Cascade); // Sletter bilder hvis RideDetails slettes

        modelBuilder.Entity<RideTrackingData>()
            .Property(r => r.TrackingPoints)
            .HasColumnType("json")  // 🚀 Bruker JSON (ikke JSONB)
            .HasConversion(
                v => JsonSerializer.Serialize(v, new JsonSerializerOptions()),  // Konverterer til JSON
                v => JsonSerializer.Deserialize<List<TrackingPoint>>(v, new JsonSerializerOptions()) ?? new List<TrackingPoint>()
            );
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

        modelBuilder.Entity<TrailFilter>()
            .HasOne(tf => tf.Trail)
            .WithOne(t => t.TrailFilters)
            .HasForeignKey<TrailFilter>(tf => tf.Id)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<TrailReview>()
            .HasOne(tr => tr.Trail)
            .WithMany(t => t.TrailReviews) //Rød Strek under TrailReviews. Feilmelding:Trail' does not contain a definition for 'TrailReviews' and no accessible extension method 'TrailReviews' accepting a first argument of type 'Trail' could be found (are you missing a using directive or an assembly reference?)CS1061
            .HasForeignKey(tr => tr.TrailId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<TrailCoordinate>()
            .HasOne(tc => tc.TrailAllCoordinates)
            .WithMany(tac => tac.Coordinates)
            .HasForeignKey(tc => tc.TrailAllCoordinatesId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<EntityImage>()
            .HasOne(ei => ei.TrailDetails)
            .WithMany(td => td.Images) 
            .HasForeignKey(ei => ei.TrailDetailsId)
            .OnDelete(DeleteBehavior.Cascade);

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
    

