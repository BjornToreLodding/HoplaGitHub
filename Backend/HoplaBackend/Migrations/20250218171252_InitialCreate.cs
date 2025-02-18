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
                name: "Images",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Url = table.Column<string>(type: "text", nullable: false),
                    ThumbnailUrl = table.Column<string>(type: "text", nullable: false),
                    Description = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Images", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Stables",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    Location = table.Column<string>(type: "text", nullable: true),
                    PrivateGroup = table.Column<bool>(type: "boolean", nullable: false),
                    ModeratedMessages = table.Column<bool>(type: "boolean", nullable: false),
                    SecretGroup = table.Column<bool>(type: "boolean", nullable: false),
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
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: true),
                    Alias = table.Column<string>(type: "text", nullable: true),
                    Email = table.Column<string>(type: "text", nullable: true),
                    PasswordHash = table.Column<string>(type: "text", nullable: true),
                    ProfilePictureUrl = table.Column<string>(type: "text", nullable: true),
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
                name: "Horses",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Breed = table.Column<string>(type: "text", nullable: true),
                    Dob = table.Column<DateTime>(type: "timestamp with time zone", nullable: true),
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
                    SUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    RUserId = table.Column<Guid>(type: "uuid", nullable: false),
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
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    StableId = table.Column<Guid>(type: "uuid", nullable: false),
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
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    StableId = table.Column<Guid>(type: "uuid", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    IsOwner = table.Column<bool>(type: "boolean", nullable: false),
                    IsAdmin = table.Column<bool>(type: "boolean", nullable: false),
                    IsModerator = table.Column<bool>(type: "boolean", nullable: false),
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
                    FromUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    ToUserId = table.Column<Guid>(type: "uuid", nullable: false),
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

            migrationBuilder.CreateTable(
                name: "EntityImages",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    ImageId = table.Column<Guid>(type: "uuid", nullable: true),
                    RideDetailId = table.Column<Guid>(type: "uuid", nullable: true),
                    TrailDetailsId = table.Column<Guid>(type: "uuid", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityImages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_EntityImages_Images_ImageId",
                        column: x => x.ImageId,
                        principalTable: "Images",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_EntityImages_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "RideDetails",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    LatMean = table.Column<double>(type: "double precision", nullable: true),
                    LongMean = table.Column<double>(type: "double precision", nullable: true),
                    LatMin = table.Column<double>(type: "double precision", nullable: true),
                    LongMin = table.Column<double>(type: "double precision", nullable: true),
                    LatMax = table.Column<double>(type: "double precision", nullable: true),
                    LongMax = table.Column<double>(type: "double precision", nullable: true),
                    JsonCoordinates50 = table.Column<string>(type: "text", nullable: true),
                    Notes = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideDetails", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "RideReviews",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Rating = table.Column<int>(type: "integer", nullable: false),
                    ReviewText = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideReviews", x => x.Id);
                    table.ForeignKey(
                        name: "FK_RideReviews_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Rides",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: false),
                    Duration = table.Column<double>(type: "double precision", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: true),
                    HorseId = table.Column<Guid>(type: "uuid", nullable: true),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Rides", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Rides_Horses_HorseId",
                        column: x => x.HorseId,
                        principalTable: "Horses",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_Rides_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "RideTrackingDatas",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    TrackingPoints = table.Column<string>(type: "json", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideTrackingDatas", x => x.Id);
                    table.ForeignKey(
                        name: "FK_RideTrackingDatas_Rides_Id",
                        column: x => x.Id,
                        principalTable: "Rides",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Trails",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: true),
                    LatMean = table.Column<double>(type: "double precision", nullable: false),
                    LongMean = table.Column<double>(type: "double precision", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    RideId = table.Column<Guid>(type: "uuid", nullable: false)
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
                name: "TrackingPoints",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    RideTrackingDataId = table.Column<Guid>(type: "uuid", nullable: false),
                    Lat = table.Column<double>(type: "double precision", nullable: false),
                    Long = table.Column<double>(type: "double precision", nullable: false),
                    TimeSinceLast = table.Column<double>(type: "double precision", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrackingPoints", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId",
                        column: x => x.RideTrackingDataId,
                        principalTable: "RideTrackingDatas",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailAllCoordinates",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailAllCoordinates", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailAllCoordinates_Trails_Id",
                        column: x => x.Id,
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailDetails",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    PictureThumbURL = table.Column<string>(type: "text", nullable: true),
                    PictureFullURL = table.Column<string>(type: "text", nullable: true),
                    LatMin = table.Column<double>(type: "double precision", nullable: true),
                    LongMin = table.Column<double>(type: "double precision", nullable: true),
                    LatMax = table.Column<double>(type: "double precision", nullable: true),
                    LongMax = table.Column<double>(type: "double precision", nullable: true),
                    JsonCoordinates50 = table.Column<string>(type: "text", nullable: true),
                    Description = table.Column<string>(type: "text", nullable: true),
                    Notes = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailDetails_Trails_Id",
                        column: x => x.Id,
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailFilters",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: false),
                    HasBridge = table.Column<bool>(type: "boolean", nullable: false),
                    Season = table.Column<string>(type: "text", nullable: false),
                    Cart = table.Column<bool>(type: "boolean", nullable: false),
                    TrafficRoads = table.Column<bool>(type: "boolean", nullable: false),
                    PeopleTraffic = table.Column<bool>(type: "boolean", nullable: false),
                    Other = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailFilters", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailFilters_Trails_Id",
                        column: x => x.Id,
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailReview",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Rating = table.Column<int>(type: "integer", nullable: false),
                    ReviewText = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailReview", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailReview_Trails_TrailId",
                        column: x => x.TrailId,
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TrailReview_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailCoordinate",
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
                        principalTable: "TrailAllCoordinates",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_ImageId",
                table: "EntityImages",
                column: "ImageId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_RideDetailId",
                table: "EntityImages",
                column: "RideDetailId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_TrailDetailsId",
                table: "EntityImages",
                column: "TrailDetailsId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_UserId",
                table: "EntityImages",
                column: "UserId");

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
                name: "IX_RideReviews_UserId",
                table: "RideReviews",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Rides_HorseId",
                table: "Rides",
                column: "HorseId");

            migrationBuilder.CreateIndex(
                name: "IX_Rides_TrailId",
                table: "Rides",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_Rides_UserId",
                table: "Rides",
                column: "UserId");

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
                name: "IX_TrackingPoints_RideTrackingDataId",
                table: "TrackingPoints",
                column: "RideTrackingDataId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailCoordinate_TrailAllCoordinatesId",
                table: "TrailCoordinate",
                column: "TrailAllCoordinatesId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailReview_TrailId",
                table: "TrailReview",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailReview_UserId",
                table: "TrailReview",
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

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages",
                column: "RideDetailId",
                principalTable: "RideDetails",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_TrailDetails_TrailDetailsId",
                table: "EntityImages",
                column: "TrailDetailsId",
                principalTable: "TrailDetails",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_RideDetails_Rides_Id",
                table: "RideDetails",
                column: "Id",
                principalTable: "Rides",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_RideReviews_Rides_Id",
                table: "RideReviews",
                column: "Id",
                principalTable: "Rides",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Rides_Trails_TrailId",
                table: "Rides",
                column: "TrailId",
                principalTable: "Trails",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Horses_Users_UserId",
                table: "Horses");

            migrationBuilder.DropForeignKey(
                name: "FK_Rides_Users_UserId",
                table: "Rides");

            migrationBuilder.DropForeignKey(
                name: "FK_Trails_Rides_RideId",
                table: "Trails");

            migrationBuilder.DropTable(
                name: "EntityImages");

            migrationBuilder.DropTable(
                name: "Messages");

            migrationBuilder.DropTable(
                name: "RideReviews");

            migrationBuilder.DropTable(
                name: "StableMessages");

            migrationBuilder.DropTable(
                name: "StableUsers");

            migrationBuilder.DropTable(
                name: "TrackingPoints");

            migrationBuilder.DropTable(
                name: "TrailCoordinate");

            migrationBuilder.DropTable(
                name: "TrailFilters");

            migrationBuilder.DropTable(
                name: "TrailReview");

            migrationBuilder.DropTable(
                name: "UserRelations");

            migrationBuilder.DropTable(
                name: "Images");

            migrationBuilder.DropTable(
                name: "RideDetails");

            migrationBuilder.DropTable(
                name: "TrailDetails");

            migrationBuilder.DropTable(
                name: "Stables");

            migrationBuilder.DropTable(
                name: "RideTrackingDatas");

            migrationBuilder.DropTable(
                name: "TrailAllCoordinates");

            migrationBuilder.DropTable(
                name: "Users");

            migrationBuilder.DropTable(
                name: "Rides");

            migrationBuilder.DropTable(
                name: "Horses");

            migrationBuilder.DropTable(
                name: "Trails");
        }
    }
}
