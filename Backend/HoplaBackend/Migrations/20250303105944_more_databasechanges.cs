using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class more_databasechanges : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages");

            migrationBuilder.DropForeignKey(
                name: "FK_TrailReview_Trails_TrailId",
                table: "TrailReview");

            migrationBuilder.DropForeignKey(
                name: "FK_TrailReview_Users_UserId",
                table: "TrailReview");

            migrationBuilder.DropTable(
                name: "RideDetails");

            migrationBuilder.DropTable(
                name: "RideReviews");

            migrationBuilder.DropTable(
                name: "TrackingPoints");

            migrationBuilder.DropTable(
                name: "RideTrackingDatas");

            migrationBuilder.DropTable(
                name: "Rides");

            migrationBuilder.DropIndex(
                name: "IX_EntityImages_RideDetailId",
                table: "EntityImages");

            migrationBuilder.DropPrimaryKey(
                name: "PK_TrailReview",
                table: "TrailReview");

            migrationBuilder.DropColumn(
                name: "RideDetailId",
                table: "EntityImages");

            migrationBuilder.RenameTable(
                name: "TrailReview",
                newName: "TrailReviews");

            migrationBuilder.RenameIndex(
                name: "IX_TrailReview_UserId",
                table: "TrailReviews",
                newName: "IX_TrailReviews_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_TrailReview_TrailId",
                table: "TrailReviews",
                newName: "IX_TrailReviews_TrailId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_TrailReviews",
                table: "TrailReviews",
                column: "Id");

            migrationBuilder.CreateTable(
                name: "MyHikes",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: true),
                    Duration = table.Column<double>(type: "double precision", nullable: false),
                    HorseId = table.Column<Guid>(type: "uuid", nullable: true),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: true),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    Secret = table.Column<bool>(type: "boolean", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MyHikes", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MyHikes_Horses_HorseId",
                        column: x => x.HorseId,
                        principalTable: "Horses",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_MyHikes_Trails_TrailId",
                        column: x => x.TrailId,
                        principalTable: "Trails",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_MyHikes_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "SubscriptionOrders",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Price = table.Column<float>(type: "real", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SubscriptionOrders", x => x.Id);
                    table.ForeignKey(
                        name: "FK_SubscriptionOrders_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailRatings",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Rating = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailRatings", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailRatings_Trails_TrailId",
                        column: x => x.TrailId,
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TrailRatings_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "UserSettings",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    DarkMode = table.Column<bool>(type: "boolean", nullable: false),
                    HideFeedFriendNewFriends = table.Column<bool>(type: "boolean", nullable: false),
                    HideFeedHorse = table.Column<bool>(type: "boolean", nullable: false),
                    HideFeedFriendHikes = table.Column<bool>(type: "boolean", nullable: false),
                    HideReactionFriendNewFriends = table.Column<bool>(type: "boolean", nullable: false),
                    HideCommentFriendNewFriends = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserSettings", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MyHikes_HorseId",
                table: "MyHikes",
                column: "HorseId");

            migrationBuilder.CreateIndex(
                name: "IX_MyHikes_TrailId",
                table: "MyHikes",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_MyHikes_UserId",
                table: "MyHikes",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_SubscriptionOrders_UserId",
                table: "SubscriptionOrders",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailRatings_TrailId",
                table: "TrailRatings",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailRatings_UserId",
                table: "TrailRatings",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailReviews_Trails_TrailId",
                table: "TrailReviews",
                column: "TrailId",
                principalTable: "Trails",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_TrailReviews_Users_UserId",
                table: "TrailReviews",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_TrailReviews_Trails_TrailId",
                table: "TrailReviews");

            migrationBuilder.DropForeignKey(
                name: "FK_TrailReviews_Users_UserId",
                table: "TrailReviews");

            migrationBuilder.DropTable(
                name: "MyHikes");

            migrationBuilder.DropTable(
                name: "SubscriptionOrders");

            migrationBuilder.DropTable(
                name: "TrailRatings");

            migrationBuilder.DropTable(
                name: "UserSettings");

            migrationBuilder.DropPrimaryKey(
                name: "PK_TrailReviews",
                table: "TrailReviews");

            migrationBuilder.RenameTable(
                name: "TrailReviews",
                newName: "TrailReview");

            migrationBuilder.RenameIndex(
                name: "IX_TrailReviews_UserId",
                table: "TrailReview",
                newName: "IX_TrailReview_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_TrailReviews_TrailId",
                table: "TrailReview",
                newName: "IX_TrailReview_TrailId");

            migrationBuilder.AddColumn<Guid>(
                name: "RideDetailId",
                table: "EntityImages",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_TrailReview",
                table: "TrailReview",
                column: "Id");

            migrationBuilder.CreateTable(
                name: "Rides",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    HorseId = table.Column<Guid>(type: "uuid", nullable: true),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: true),
                    UserId = table.Column<Guid>(type: "uuid", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Duration = table.Column<double>(type: "double precision", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: false)
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
                        name: "FK_Rides_Trails_TrailId",
                        column: x => x.TrailId,
                        principalTable: "Trails",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_Rides_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "RideDetails",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    JsonCoordinates50 = table.Column<string>(type: "text", nullable: true),
                    LatMax = table.Column<double>(type: "double precision", nullable: true),
                    LatMean = table.Column<double>(type: "double precision", nullable: true),
                    LatMin = table.Column<double>(type: "double precision", nullable: true),
                    LongMax = table.Column<double>(type: "double precision", nullable: true),
                    LongMean = table.Column<double>(type: "double precision", nullable: true),
                    LongMin = table.Column<double>(type: "double precision", nullable: true),
                    Notes = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_RideDetails_Rides_Id",
                        column: x => x.Id,
                        principalTable: "Rides",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "RideReviews",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Rating = table.Column<int>(type: "integer", nullable: false),
                    ReviewText = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RideReviews", x => x.Id);
                    table.ForeignKey(
                        name: "FK_RideReviews_Rides_Id",
                        column: x => x.Id,
                        principalTable: "Rides",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_RideReviews_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
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

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_RideDetailId",
                table: "EntityImages",
                column: "RideDetailId");

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
                name: "IX_TrackingPoints_RideTrackingDataId",
                table: "TrackingPoints",
                column: "RideTrackingDataId");

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages",
                column: "RideDetailId",
                principalTable: "RideDetails",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_TrailReview_Trails_TrailId",
                table: "TrailReview",
                column: "TrailId",
                principalTable: "Trails",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_TrailReview_Users_UserId",
                table: "TrailReview",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
