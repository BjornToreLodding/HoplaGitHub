using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class ChangedSomeNamesInTrailFilters : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.RenameColumn(
                name: "FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                newName: "TrailFilterDefinitionId");

            migrationBuilder.RenameIndex(
                name: "IX_TrailFilterValues_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                newName: "IX_TrailFilterValues_TrailFilterDefinitionId");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_TrailFilterDefinit~",
                schema: "public",
                table: "TrailFilterValues",
                column: "TrailFilterDefinitionId",
                principalSchema: "public",
                principalTable: "TrailFilterDefinitions",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_TrailFilterDefinit~",
                schema: "public",
                table: "TrailFilterValues");

            migrationBuilder.RenameColumn(
                name: "TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                newName: "FilterDefinitionId");

            migrationBuilder.RenameIndex(
                name: "IX_TrailFilterValues_TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                newName: "IX_TrailFilterValues_FilterDefinitionId");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailFilterValues_TrailFilterDefinitions_FilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                column: "FilterDefinitionId",
                principalSchema: "public",
                principalTable: "TrailFilterDefinitions",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
