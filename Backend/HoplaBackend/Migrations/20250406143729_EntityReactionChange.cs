using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class EntityReactionChange : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<string>(
                name: "Reaction",
                schema: "public",
                table: "EntityReactions",
                type: "text",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<int>(
                name: "Reaction",
                schema: "public",
                table: "EntityReactions",
                type: "integer",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "text");
        }
    }
}
