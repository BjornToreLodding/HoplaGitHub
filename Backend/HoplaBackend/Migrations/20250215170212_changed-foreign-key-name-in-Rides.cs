using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class changedforeignkeynameinRides : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Rides_Trails_RideId",
                table: "Rides");

            migrationBuilder.DropIndex(
                name: "IX_Rides_RideId",
                table: "Rides");

            migrationBuilder.DropColumn(
                name: "RideId",
                table: "Rides");

            migrationBuilder.CreateIndex(
                name: "IX_Rides_TrailId",
                table: "Rides",
                column: "TrailId");

            migrationBuilder.AddForeignKey(
                name: "FK_Rides_Trails_TrailId",
                table: "Rides",
                column: "TrailId",
                principalTable: "Trails",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Rides_Trails_TrailId",
                table: "Rides");

            migrationBuilder.DropIndex(
                name: "IX_Rides_TrailId",
                table: "Rides");

            migrationBuilder.AddColumn<int>(
                name: "RideId",
                table: "Rides",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Rides_RideId",
                table: "Rides",
                column: "RideId");

            migrationBuilder.AddForeignKey(
                name: "FK_Rides_Trails_RideId",
                table: "Rides",
                column: "RideId",
                principalTable: "Trails",
                principalColumn: "Id");
        }
    }
}
