using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailsRiderelation : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateIndex(
                name: "IX_Trails_RideId",
                table: "Trails",
                column: "RideId");

            migrationBuilder.AddForeignKey(
                name: "FK_Trails_Rides_RideId",
                table: "Trails",
                column: "RideId",
                principalTable: "Rides",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Trails_Rides_RideId",
                table: "Trails");

            migrationBuilder.DropIndex(
                name: "IX_Trails_RideId",
                table: "Trails");
        }
    }
}
