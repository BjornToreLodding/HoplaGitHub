using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class SystemSettingsKeyNoId : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_SystemSettings",
                schema: "public",
                table: "SystemSettings");

            migrationBuilder.DropColumn(
                name: "Id",
                schema: "public",
                table: "SystemSettings");

            migrationBuilder.AddPrimaryKey(
                name: "PK_SystemSettings",
                schema: "public",
                table: "SystemSettings",
                column: "Key");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_SystemSettings",
                schema: "public",
                table: "SystemSettings");

            migrationBuilder.AddColumn<int>(
                name: "Id",
                schema: "public",
                table: "SystemSettings",
                type: "integer",
                nullable: false,
                defaultValue: 0)
                .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

            migrationBuilder.AddPrimaryKey(
                name: "PK_SystemSettings",
                schema: "public",
                table: "SystemSettings",
                column: "Id");
        }
    }
}
