using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailFilltersMoreData : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.InsertData(
                schema: "public",
                table: "TrailFilterDefinitions",
                columns: new[] { "Id", "DefaultValue", "DisplayName", "IsActive", "Name", "OptionsJson", "Order", "Type" },
                values: new object[,]
                {
                    { new Guid("12345678-0000-0000-0101-123456780003"), "Gravel", "Underlag2", true, "SurfaceType", "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]", 3, 2 },
                    { new Guid("12345678-0000-0000-0101-123456780004"), "Easy", "Vanskelighetsgrad2", true, "Difficulty", "[\"Easy\",\"Medium\",\"Hard\"]", 4, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780005"), "false", "Åpen om vinteren", true, "WinterAccessible", null, 5, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780006"), "false", "Har bro over elv", true, "HasBridge", null, 6, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780007"), "false", "Tilrettelagt for vogn", true, "StrollerFriendly", null, 7, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780008"), "Lite", "Biltrafikk", true, "TrafficLevel", "[\"Ingen\",\"Lite\",\"Middels\",\"Mye\"]", 8, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780009"), "Noe", "Folk langs veien", true, "CrowdLevel", "[\"Sjelden\",\"Noe\",\"Mye\"]", 9, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780010"), "false", "Egner seg for barnevogn", true, "SuitableForChildren", null, 10, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780011"), "true", "Skogsområde", true, "ForestArea", null, 11, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780012"), "false", "Mulighet for bading", true, "SwimmingSpot", null, 12, 0 }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780003"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780004"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780005"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780006"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780007"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780008"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780009"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780010"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780011"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780012"));
        }
    }
}
