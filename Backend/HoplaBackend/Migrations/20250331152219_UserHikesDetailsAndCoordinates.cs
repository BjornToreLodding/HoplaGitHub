using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class UserHikesDetailsAndCoordinates : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "TrailCoordinate",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailCoordinate50",
                schema: "public");

            migrationBuilder.DropColumn(
                name: "Length",
                schema: "public",
                table: "UserHikes");

            migrationBuilder.AddColumn<double>(
                name: "Distance",
                schema: "public",
                table: "UserHikes",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<DateTime>(
                name: "StartedAt",
                schema: "public",
                table: "UserHikes",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<string>(
                name: "Title",
                schema: "public",
                table: "UserHikes",
                type: "text",
                nullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "LongMin",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "double precision",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "LongMax",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "double precision",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "LatMin",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "double precision",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "LatMax",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "double precision",
                oldNullable: true);

            migrationBuilder.AlterColumn<string>(
                name: "Description",
                schema: "public",
                table: "TrailDetails",
                type: "text",
                nullable: false,
                defaultValue: "",
                oldClrType: typeof(string),
                oldType: "text",
                oldNullable: true);

            migrationBuilder.AddColumn<string>(
                name: "PreviewCoordinatesCsv",
                schema: "public",
                table: "TrailDetails",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "CoordinatesCsv",
                schema: "public",
                table: "TrailAllCoordinates",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.CreateTable(
                name: "UserHikeDetails",
                schema: "public",
                columns: table => new
                {
                    UserHikeId = table.Column<Guid>(type: "uuid", nullable: false),
                    LatMin = table.Column<double>(type: "double precision", nullable: false),
                    LatMax = table.Column<double>(type: "double precision", nullable: false),
                    LatMean = table.Column<double>(type: "double precision", nullable: false),
                    LongMin = table.Column<double>(type: "double precision", nullable: false),
                    LongMax = table.Column<double>(type: "double precision", nullable: false),
                    LongMean = table.Column<double>(type: "double precision", nullable: false),
                    Description = table.Column<string>(type: "text", nullable: true),
                    CoordinatesCsv = table.Column<string>(type: "text", nullable: false),
                    UserHikeId1 = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserHikeDetails", x => x.UserHikeId);
                    table.ForeignKey(
                        name: "FK_UserHikeDetails_UserHikes_UserHikeId1",
                        column: x => x.UserHikeId1,
                        principalSchema: "public",
                        principalTable: "UserHikes",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_UserHikeDetails_UserHikeId1",
                schema: "public",
                table: "UserHikeDetails",
                column: "UserHikeId1");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "UserHikeDetails",
                schema: "public");

            migrationBuilder.DropColumn(
                name: "Distance",
                schema: "public",
                table: "UserHikes");

            migrationBuilder.DropColumn(
                name: "StartedAt",
                schema: "public",
                table: "UserHikes");

            migrationBuilder.DropColumn(
                name: "Title",
                schema: "public",
                table: "UserHikes");

            migrationBuilder.DropColumn(
                name: "PreviewCoordinatesCsv",
                schema: "public",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "CoordinatesCsv",
                schema: "public",
                table: "TrailAllCoordinates");

            migrationBuilder.AddColumn<double>(
                name: "Length",
                schema: "public",
                table: "UserHikes",
                type: "double precision",
                nullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "LongMin",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "double precision");

            migrationBuilder.AlterColumn<double>(
                name: "LongMax",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "double precision");

            migrationBuilder.AlterColumn<double>(
                name: "LatMin",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "double precision");

            migrationBuilder.AlterColumn<double>(
                name: "LatMax",
                schema: "public",
                table: "TrailDetails",
                type: "double precision",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "double precision");

            migrationBuilder.AlterColumn<string>(
                name: "Description",
                schema: "public",
                table: "TrailDetails",
                type: "text",
                nullable: true,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.CreateTable(
                name: "TrailCoordinate",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    TrailAllCoordinatesId = table.Column<Guid>(type: "uuid", nullable: false),
                    Lat = table.Column<double>(type: "double precision", nullable: false),
                    Long = table.Column<double>(type: "double precision", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailCoordinate", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailCoordinate_TrailAllCoordinates_TrailAllCoordinatesId",
                        column: x => x.TrailAllCoordinatesId,
                        principalSchema: "public",
                        principalTable: "TrailAllCoordinates",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

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

            migrationBuilder.CreateIndex(
                name: "IX_TrailCoordinate_TrailAllCoordinatesId",
                schema: "public",
                table: "TrailCoordinate",
                column: "TrailAllCoordinatesId");
        }
    }
}
