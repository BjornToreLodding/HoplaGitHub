using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailFilterValieFKFix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_TrailFilterDefinit~",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.DropIndex(
                name: "IX_TrailFilterValues_TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780001"));

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780002"));

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

            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780013"));

            migrationBuilder.DropColumn(
                name: "TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.CreateIndex(
                name: "IX_TrailFilterValues_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                column: "FilterDefinitionId");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                column: "FilterDefinitionId",
                principalSchema: "public",
                principalTable: "TrailFilterDefinitions",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.DropIndex(
                name: "IX_TrailFilterValues_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.AddColumn<Guid>(
                name: "TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.InsertData(
                schema: "public",
                table: "TrailFilterDefinitions",
                columns: new[] { "Id", "DefaultValue", "DisplayName", "IsActive", "Name", "OptionsJson", "Order", "Type" },
                values: new object[,]
                {
                    { new Guid("12345678-0000-0000-0101-123456780001"), "Gravel", "Underlag", true, "SurfaceType1", "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]", 1, 2 },
                    { new Guid("12345678-0000-0000-0101-123456780002"), "Easy", "Vanskelighetsgrad", true, "Difficulty1", "[\"Easy\",\"Medium\",\"Hard\"]", 2, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780003"), "Gravel", "Underlag2", true, "SurfaceType2", "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]", 3, 2 },
                    { new Guid("12345678-0000-0000-0101-123456780004"), "Easy", "Vanskelighetsgrad2", true, "Difficulty2", "[\"Easy\",\"Medium\",\"Hard\"]", 4, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780005"), "false", "Åpen om vinteren", true, "WinterAccessible", null, 5, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780006"), "false", "Har bro over elv", true, "HasBridge", null, 6, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780007"), "false", "Tilrettelagt for vogn", true, "StrollerFriendly", null, 7, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780008"), "Lite", "Biltrafikk", true, "TrafficLevel", "[\"Ingen\",\"Lite\",\"Middels\",\"Mye\"]", 8, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780009"), "Noe", "Folk langs veien", true, "CrowdLevel", "[\"Sjelden\",\"Noe\",\"Mye\"]", 9, 1 },
                    { new Guid("12345678-0000-0000-0101-123456780010"), "false", "Egner seg for barnevogn", true, "SuitableForChildren", null, 10, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780011"), "true", "Skogsområde", true, "ForestArea", null, 11, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780012"), "false", "Mulighet for bading", true, "SwimmingSpot", null, 12, 0 },
                    { new Guid("12345678-0000-0000-0101-123456780013"), "0", "Mengde innsekter", true, "Insects", null, 13, 3 }
                });

            migrationBuilder.CreateIndex(
                name: "IX_TrailFilterValues_TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                column: "TrailFilterDefinitionId");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_TrailFilterDefinit~",
                schema: "public",
                table: "TrailFilterValues",
                column: "TrailFilterDefinitionId",
                principalSchema: "public",
                principalTable: "TrailFilterDefinitions",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
