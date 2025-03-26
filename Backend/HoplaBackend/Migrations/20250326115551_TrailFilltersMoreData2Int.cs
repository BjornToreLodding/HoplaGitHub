using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class TrailFilltersMoreData2Int : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780001"),
                column: "Name",
                value: "SurfaceType1");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780002"),
                column: "Name",
                value: "Difficulty1");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780003"),
                column: "Name",
                value: "SurfaceType2");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780004"),
                column: "Name",
                value: "Difficulty2");

            migrationBuilder.InsertData(
                schema: "public",
                table: "TrailFilterDefinitions",
                columns: new[] { "Id", "DefaultValue", "DisplayName", "IsActive", "Name", "OptionsJson", "Order", "Type" },
                values: new object[] { new Guid("12345678-0000-0000-0101-123456780013"), "0", "Mengde innsekter", true, "Insects", null, 13, 3 });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DeleteData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780013"));

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780001"),
                column: "Name",
                value: "SurfaceType");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780002"),
                column: "Name",
                value: "Difficulty");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780003"),
                column: "Name",
                value: "SurfaceType");

            migrationBuilder.UpdateData(
                schema: "public",
                table: "TrailFilterDefinitions",
                keyColumn: "Id",
                keyValue: new Guid("12345678-0000-0000-0101-123456780004"),
                column: "Name",
                value: "Difficulty");
        }
    }
}
