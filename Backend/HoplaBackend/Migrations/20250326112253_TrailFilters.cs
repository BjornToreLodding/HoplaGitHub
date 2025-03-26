using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailFilters : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "DataType",
                schema: "public",
                table: "TrailFilterDefinitions",
                newName: "DisplayName");

            migrationBuilder.AddColumn<string>(
                name: "DefaultValue",
                schema: "public",
                table: "TrailFilterDefinitions",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<bool>(
                name: "IsActive",
                schema: "public",
                table: "TrailFilterDefinitions",
                type: "boolean",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<string>(
                name: "OptionsJson",
                schema: "public",
                table: "TrailFilterDefinitions",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "Order",
                schema: "public",
                table: "TrailFilterDefinitions",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "Type",
                schema: "public",
                table: "TrailFilterDefinitions",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.InsertData(
                schema: "public",
                table: "TrailFilterDefinitions",
                columns: new[] { "Id", "DefaultValue", "DisplayName", "IsActive", "Name", "OptionsJson", "Order", "Type" },
                values: new object[,]
                {
                    { new Guid("12345678-0000-0000-0101-123456780001"), "Gravel", "Underlag", true, "SurfaceType", "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]", 1, 2 },
                    { new Guid("12345678-0000-0000-0101-123456780002"), "Easy", "Vanskelighetsgrad", true, "Difficulty", "[\"Easy\",\"Medium\",\"Hard\"]", 2, 1 }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
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

            migrationBuilder.DropColumn(
                name: "DefaultValue",
                schema: "public",
                table: "TrailFilterDefinitions");

            migrationBuilder.DropColumn(
                name: "IsActive",
                schema: "public",
                table: "TrailFilterDefinitions");

            migrationBuilder.DropColumn(
                name: "OptionsJson",
                schema: "public",
                table: "TrailFilterDefinitions");

            migrationBuilder.DropColumn(
                name: "Order",
                schema: "public",
                table: "TrailFilterDefinitions");

            migrationBuilder.DropColumn(
                name: "Type",
                schema: "public",
                table: "TrailFilterDefinitions");

            migrationBuilder.RenameColumn(
                name: "DisplayName",
                schema: "public",
                table: "TrailFilterDefinitions",
                newName: "DataType");
        }
    }
}
