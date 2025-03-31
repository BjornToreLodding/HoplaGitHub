using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class UserHikesDetailsAndCoordinatesProblems : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_UserHikeDetails_UserHikes_UserHikeId1",
                schema: "public",
                table: "UserHikeDetails");

            migrationBuilder.DropIndex(
                name: "IX_UserHikeDetails_UserHikeId1",
                schema: "public",
                table: "UserHikeDetails");

            migrationBuilder.DropColumn(
                name: "UserHikeId1",
                schema: "public",
                table: "UserHikeDetails");

            migrationBuilder.AddForeignKey(
                name: "FK_UserHikeDetails_UserHikes_UserHikeId",
                schema: "public",
                table: "UserHikeDetails",
                column: "UserHikeId",
                principalSchema: "public",
                principalTable: "UserHikes",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_UserHikeDetails_UserHikes_UserHikeId",
                schema: "public",
                table: "UserHikeDetails");

            migrationBuilder.AddColumn<Guid>(
                name: "UserHikeId1",
                schema: "public",
                table: "UserHikeDetails",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.CreateIndex(
                name: "IX_UserHikeDetails_UserHikeId1",
                schema: "public",
                table: "UserHikeDetails",
                column: "UserHikeId1");

            migrationBuilder.AddForeignKey(
                name: "FK_UserHikeDetails_UserHikes_UserHikeId1",
                schema: "public",
                table: "UserHikeDetails",
                column: "UserHikeId1",
                principalSchema: "public",
                principalTable: "UserHikes",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
