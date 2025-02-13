using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Filters",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Cart = table.Column<bool>(type: "boolean", nullable: true),
                    Bridge = table.Column<bool>(type: "boolean", nullable: true),
                    Difficulty = table.Column<string>(type: "text", nullable: true),
                    Traffic = table.Column<string>(type: "text", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Filters", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "RideDetails",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    CoordinateslistShort = table.Column<string>(type: "text", nullable: true),
                    CoordinatesAll = table.Column<string>(type: "text", nullable: true),
                    Picturethumb = table.Column<byte[]>(type: "bytea", nullable: true),
                    Picturefull = table.Column<byte[]>(type: "bytea", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideDetails", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Rides",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    UserId = table.Column<int>(type: "integer", nullable: true),
                    HorseId = table.Column<int>(type: "integer", nullable: true),
                    Date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: true),
                    Duration = table.Column<int>(type: "integer", nullable: true),
                    LatMean = table.Column<double>(type: "double precision", nullable: true),
                    LongMean = table.Column<double>(type: "double precision", nullable: true),
                    LatStart = table.Column<double>(type: "double precision", nullable: true),
                    LongStart = table.Column<double>(type: "double precision", nullable: true),
                    LatEnd = table.Column<double>(type: "double precision", nullable: true),
                    LongEnd = table.Column<double>(type: "double precision", nullable: true),
                    CoordinateslistShort = table.Column<string>(type: "text", nullable: true),
                    CoordinatesAll = table.Column<string>(type: "text", nullable: true),
                    Picturethumb = table.Column<byte[]>(type: "bytea", nullable: true),
                    Picturefull = table.Column<byte[]>(type: "bytea", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Rides", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Stables",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "text", nullable: false),
                    Location = table.Column<string>(type: "text", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Stables", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Users",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Alias = table.Column<string>(type: "text", nullable: true),
                    Name = table.Column<string>(type: "text", nullable: true),
                    Email = table.Column<string>(type: "text", nullable: true),
                    PasswordHash = table.Column<string>(type: "text", nullable: true),
                    Admin = table.Column<bool>(type: "boolean", nullable: false),
                    Premium = table.Column<bool>(type: "boolean", nullable: false),
                    VerifiedTrail = table.Column<bool>(type: "boolean", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Dob = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Trails",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    RideId = table.Column<int>(type: "integer", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    Beskrivelse = table.Column<string>(type: "text", nullable: true),
                    FilterId = table.Column<int>(type: "integer", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Trails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Trails_Rides_RideId",
                        column: x => x.RideId,
                        principalTable: "Rides",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Horses",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "text", nullable: false),
                    UserId = table.Column<int>(type: "integer", nullable: false),
                    Breed = table.Column<string>(type: "text", nullable: true),
                    Age = table.Column<int>(type: "integer", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Horses", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Horses_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Messages",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    SUserId = table.Column<int>(type: "integer", nullable: false),
                    RUserId = table.Column<int>(type: "integer", nullable: false),
                    MessageText = table.Column<string>(type: "text", nullable: false),
                    SentAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Messages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Messages_Users_RUserId",
                        column: x => x.RUserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Messages_Users_SUserId",
                        column: x => x.SUserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "StableMessages",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    UserId = table.Column<int>(type: "integer", nullable: false),
                    StableId = table.Column<int>(type: "integer", nullable: false),
                    MessageText = table.Column<string>(type: "text", nullable: false),
                    SentAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StableMessages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_StableMessages_Stables_StableId",
                        column: x => x.StableId,
                        principalTable: "Stables",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StableMessages_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "StableUsers",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    UserId = table.Column<int>(type: "integer", nullable: false),
                    StableId = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    StableAdmin = table.Column<bool>(type: "boolean", nullable: false),
                    StableModerator = table.Column<bool>(type: "boolean", nullable: false),
                    NotifyNewMessage = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StableUsers", x => x.Id);
                    table.ForeignKey(
                        name: "FK_StableUsers_Stables_StableId",
                        column: x => x.StableId,
                        principalTable: "Stables",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StableUsers_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "UserRelations",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    FromUserId = table.Column<int>(type: "integer", nullable: false),
                    ToUserId = table.Column<int>(type: "integer", nullable: false),
                    Status = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserRelations", x => x.Id);
                    table.ForeignKey(
                        name: "FK_UserRelations_Users_FromUserId",
                        column: x => x.FromUserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_UserRelations_Users_ToUserId",
                        column: x => x.ToUserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Horses_UserId",
                table: "Horses",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Messages_RUserId",
                table: "Messages",
                column: "RUserId");

            migrationBuilder.CreateIndex(
                name: "IX_Messages_SUserId",
                table: "Messages",
                column: "SUserId");

            migrationBuilder.CreateIndex(
                name: "IX_StableMessages_StableId",
                table: "StableMessages",
                column: "StableId");

            migrationBuilder.CreateIndex(
                name: "IX_StableMessages_UserId",
                table: "StableMessages",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_StableUsers_StableId",
                table: "StableUsers",
                column: "StableId");

            migrationBuilder.CreateIndex(
                name: "IX_StableUsers_UserId",
                table: "StableUsers",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Trails_RideId",
                table: "Trails",
                column: "RideId");

            migrationBuilder.CreateIndex(
                name: "IX_UserRelations_FromUserId",
                table: "UserRelations",
                column: "FromUserId");

            migrationBuilder.CreateIndex(
                name: "IX_UserRelations_ToUserId",
                table: "UserRelations",
                column: "ToUserId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Filters");

            migrationBuilder.DropTable(
                name: "Horses");

            migrationBuilder.DropTable(
                name: "Messages");

            migrationBuilder.DropTable(
                name: "RideDetails");

            migrationBuilder.DropTable(
                name: "StableMessages");

            migrationBuilder.DropTable(
                name: "StableUsers");

            migrationBuilder.DropTable(
                name: "Trails");

            migrationBuilder.DropTable(
                name: "UserRelations");

            migrationBuilder.DropTable(
                name: "Stables");

            migrationBuilder.DropTable(
                name: "Rides");

            migrationBuilder.DropTable(
                name: "Users");
        }
    }
}
