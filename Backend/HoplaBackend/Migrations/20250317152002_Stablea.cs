using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class Stablea : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Description",
                schema: "public",
                table: "Stables",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<double>(
                name: "Latitude",
                schema: "public",
                table: "Stables",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<double>(
                name: "Longitude",
                schema: "public",
                table: "Stables",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<string>(
                name: "PictureUrl",
                schema: "public",
                table: "Stables",
                type: "text",
                nullable: false,
                defaultValue: "");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Description",
                schema: "public",
                table: "Stables");

            migrationBuilder.DropColumn(
                name: "Latitude",
                schema: "public",
                table: "Stables");

            migrationBuilder.DropColumn(
                name: "Longitude",
                schema: "public",
                table: "Stables");

            migrationBuilder.DropColumn(
                name: "PictureUrl",
                schema: "public",
                table: "Stables");
        }
    }
}
