using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class EntityFeed : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Trails_Rides_RideId",
                table: "Trails");

            migrationBuilder.DropIndex(
                name: "IX_Trails_RideId",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "RideId",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "PictureFullURL",
                table: "TrailDetails");

            migrationBuilder.RenameColumn(
                name: "PictureThumbURL",
                table: "TrailDetails",
                newName: "PictureUrl");

            migrationBuilder.AddColumn<double>(
                name: "Distance",
                table: "Trails",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<Guid>(
                name: "UserId",
                table: "Trails",
                type: "uuid",
                nullable: true);

            migrationBuilder.AlterColumn<DateTime>(
                name: "CreatedAt",
                table: "Horses",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified),
                oldClrType: typeof(DateTime),
                oldType: "timestamp with time zone",
                oldNullable: true);

            migrationBuilder.CreateTable(
                name: "EntityFeed",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    ActionType = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityFeed", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Trails_UserId",
                table: "Trails",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_Trails_Users_UserId",
                table: "Trails",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Trails_Users_UserId",
                table: "Trails");

            migrationBuilder.DropTable(
                name: "EntityFeed");

            migrationBuilder.DropIndex(
                name: "IX_Trails_UserId",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "Distance",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "UserId",
                table: "Trails");

            migrationBuilder.RenameColumn(
                name: "PictureUrl",
                table: "TrailDetails",
                newName: "PictureThumbURL");

            migrationBuilder.AddColumn<Guid>(
                name: "RideId",
                table: "Trails",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<string>(
                name: "PictureFullURL",
                table: "TrailDetails",
                type: "text",
                nullable: true);

            migrationBuilder.AlterColumn<DateTime>(
                name: "CreatedAt",
                table: "Horses",
                type: "timestamp with time zone",
                nullable: true,
                oldClrType: typeof(DateTime),
                oldType: "timestamp with time zone");

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
    }
}
