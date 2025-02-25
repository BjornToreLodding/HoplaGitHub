using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class horsePictures : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "HorsePictureUrl",
                table: "Horses",
                type: "text",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "HorsePictureUrl",
                table: "Horses");
        }
    }
}
