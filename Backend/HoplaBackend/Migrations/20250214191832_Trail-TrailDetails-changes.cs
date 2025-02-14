using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailTrailDetailschanges : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateTime>(
                name: "CreatedAt",
                table: "Trails",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<double>(
                name: "LatMax",
                table: "TrailDetails",
                type: "double precision",
                nullable: true);

            migrationBuilder.AddColumn<double>(
                name: "LatMin",
                table: "TrailDetails",
                type: "double precision",
                nullable: true);

            migrationBuilder.AddColumn<double>(
                name: "LongMax",
                table: "TrailDetails",
                type: "double precision",
                nullable: true);

            migrationBuilder.AddColumn<double>(
                name: "LongMin",
                table: "TrailDetails",
                type: "double precision",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "PictureFullURL",
                table: "TrailDetails",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "PictureThumbURL",
                table: "TrailDetails",
                type: "text",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "CreatedAt",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "LatMax",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "LatMin",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "LongMax",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "LongMin",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "PictureFullURL",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "PictureThumbURL",
                table: "TrailDetails");
        }
    }
}
