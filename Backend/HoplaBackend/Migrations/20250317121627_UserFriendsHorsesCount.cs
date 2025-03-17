using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class UserFriendsHorsesCount : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "FriendsCount",
                schema: "public",
                table: "Users",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "HorseCount",
                schema: "public",
                table: "Users",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<DateTime>(
                name: "Closed",
                schema: "public",
                table: "UserReports",
                type: "timestamp with time zone",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "FeedBack",
                schema: "public",
                table: "UserReports",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<DateTime>(
                name: "InProgress",
                schema: "public",
                table: "UserReports",
                type: "timestamp with time zone",
                nullable: true);

            migrationBuilder.AddColumn<DateTime>(
                name: "Resolved",
                schema: "public",
                table: "UserReports",
                type: "timestamp with time zone",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "FriendsCount",
                schema: "public",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "HorseCount",
                schema: "public",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "Closed",
                schema: "public",
                table: "UserReports");

            migrationBuilder.DropColumn(
                name: "FeedBack",
                schema: "public",
                table: "UserReports");

            migrationBuilder.DropColumn(
                name: "InProgress",
                schema: "public",
                table: "UserReports");

            migrationBuilder.DropColumn(
                name: "Resolved",
                schema: "public",
                table: "UserReports");
        }
    }
}
