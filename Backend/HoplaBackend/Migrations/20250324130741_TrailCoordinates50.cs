using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailCoordinates50 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "JsonCoordinates50",
                schema: "public",
                table: "TrailDetails");

            migrationBuilder.CreateTable(
                name: "TrailCoordinate50",
                schema: "public",
                columns: table => new
                {
                    TrailDetailId = table.Column<Guid>(type: "uuid", nullable: false),
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Lat = table.Column<double>(type: "double precision", nullable: false),
                    Long = table.Column<double>(type: "double precision", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailCoordinate50", x => new { x.TrailDetailId, x.Id });
                    table.ForeignKey(
                        name: "FK_TrailCoordinate50_TrailDetails_TrailDetailId",
                        column: x => x.TrailDetailId,
                        principalSchema: "public",
                        principalTable: "TrailDetails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "TrailCoordinate50",
                schema: "public");

            migrationBuilder.AddColumn<string>(
                name: "JsonCoordinates50",
                schema: "public",
                table: "TrailDetails",
                type: "text",
                nullable: true);
        }
    }
}
