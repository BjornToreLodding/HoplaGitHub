using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class changesinRideTrackingDatas : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrackingPoint_RideTrackingDatas_RideTrackingDataId",
                table: "TrackingPoint");

            migrationBuilder.DropPrimaryKey(
                name: "PK_TrackingPoint",
                table: "TrackingPoint");

            migrationBuilder.RenameTable(
                name: "TrackingPoint",
                newName: "TrackingPoints");

            migrationBuilder.RenameIndex(
                name: "IX_TrackingPoint_RideTrackingDataId",
                table: "TrackingPoints",
                newName: "IX_TrackingPoints_RideTrackingDataId");

            migrationBuilder.AddColumn<string>(
                name: "TrackingPoints",
                table: "RideTrackingDatas",
                type: "json",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddPrimaryKey(
                name: "PK_TrackingPoints",
                table: "TrackingPoints",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId",
                table: "TrackingPoints",
                column: "RideTrackingDataId",
                principalTable: "RideTrackingDatas",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId",
                table: "TrackingPoints");

            migrationBuilder.DropPrimaryKey(
                name: "PK_TrackingPoints",
                table: "TrackingPoints");

            migrationBuilder.DropColumn(
                name: "TrackingPoints",
                table: "RideTrackingDatas");

            migrationBuilder.RenameTable(
                name: "TrackingPoints",
                newName: "TrackingPoint");

            migrationBuilder.RenameIndex(
                name: "IX_TrackingPoints_RideTrackingDataId",
                table: "TrackingPoint",
                newName: "IX_TrackingPoint_RideTrackingDataId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_TrackingPoint",
                table: "TrackingPoint",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_TrackingPoint_RideTrackingDatas_RideTrackingDataId",
                table: "TrackingPoint",
                column: "RideTrackingDataId",
                principalTable: "RideTrackingDatas",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
