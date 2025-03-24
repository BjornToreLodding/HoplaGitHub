using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailCondition : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "Rating",
                schema: "public",
                table: "TrailReviews",
                newName: "Condition");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "Condition",
                schema: "public",
                table: "TrailReviews",
                newName: "Rating");
        }
    }
}
