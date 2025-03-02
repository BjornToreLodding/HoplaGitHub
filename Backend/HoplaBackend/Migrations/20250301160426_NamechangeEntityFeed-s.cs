using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class NamechangeEntityFeeds : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_EntityFeed",
                table: "EntityFeed");

            migrationBuilder.RenameTable(
                name: "EntityFeed",
                newName: "EntityFeeds");

            migrationBuilder.AddPrimaryKey(
                name: "PK_EntityFeeds",
                table: "EntityFeeds",
                column: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_EntityFeeds",
                table: "EntityFeeds");

            migrationBuilder.RenameTable(
                name: "EntityFeeds",
                newName: "EntityFeed");

            migrationBuilder.AddPrimaryKey(
                name: "PK_EntityFeed",
                table: "EntityFeed",
                column: "Id");
        }
    }
}
