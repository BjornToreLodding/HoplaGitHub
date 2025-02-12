using MyApp.Models;
using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using System.Text.RegularExpressions;

namespace MyApp.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<User> Users { get; set; }
    public DbSet<FriendRequest> FriendRequests { get; set; } // Endret fra Friendrequest til FriendRequest
    public DbSet<Message> Messages { get; set; }
    public DbSet<Filter> Filters { get; set; }
    public DbSet<Horse> Horses { get; set; }
    public DbSet<Ride> Rides { get; set; }
    public DbSet<RideDetail> RideDetails { get; set; }
    public DbSet<Stable> Stables { get; set; }
    public DbSet<StableUser> StableUsers { get; set; } 
    public DbSet<StableMessage> StableMessages { get; set; }
    public DbSet<Trail> Trails { get; set; }



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
        
       // modelBuilder.Entity<StableUser>().HasKey(su => su.Id);  // 📌 Tvinger EF til å bruke Id som primærnøkkel


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
    
}
