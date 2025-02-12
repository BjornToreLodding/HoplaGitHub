using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class friendrequestuserFKandUserAlias : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Alias",
                table: "Users",
                type: "text",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_FriendRequests_FromUserId",
                table: "FriendRequests",
                column: "FromUserId");

            migrationBuilder.CreateIndex(
                name: "IX_FriendRequests_ToUserId",
                table: "FriendRequests",
                column: "ToUserId");

            migrationBuilder.AddForeignKey(
                name: "FK_FriendRequests_Users_FromUserId",
                table: "FriendRequests",
                column: "FromUserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_FriendRequests_Users_ToUserId",
                table: "FriendRequests",
                column: "ToUserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_FriendRequests_Users_FromUserId",
                table: "FriendRequests");

            migrationBuilder.DropForeignKey(
                name: "FK_FriendRequests_Users_ToUserId",
                table: "FriendRequests");

            migrationBuilder.DropIndex(
                name: "IX_FriendRequests_FromUserId",
                table: "FriendRequests");

            migrationBuilder.DropIndex(
                name: "IX_FriendRequests_ToUserId",
                table: "FriendRequests");

            migrationBuilder.DropColumn(
                name: "Alias",
                table: "Users");
        }
    }
}
