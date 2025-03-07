using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class UserHike : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.EnsureSchema(
                name: "public");

            migrationBuilder.CreateTable(
                name: "EntityFeeds",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    EntityTitle = table.Column<string>(type: "text", nullable: true),
                    EntityObject = table.Column<string>(type: "text", nullable: true),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    ActionType = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityFeeds", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Images",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    PictureUrl = table.Column<string>(type: "text", nullable: false),
                    Description = table.Column<string>(type: "text", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Images", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Stables",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    Location = table.Column<string>(type: "text", nullable: true),
                    PrivateGroup = table.Column<bool>(type: "boolean", nullable: false),
                    ModeratedMessages = table.Column<bool>(type: "boolean", nullable: false),
                    SecretGroup = table.Column<bool>(type: "boolean", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Stables", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SystemSettings",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Key = table.Column<string>(type: "text", nullable: false),
                    Value = table.Column<string>(type: "text", nullable: false),
                    Type = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SystemSettings", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "TrailFilterDefinitions",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    DataType = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailFilterDefinitions", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Users",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: true),
                    Alias = table.Column<string>(type: "text", nullable: true),
                    Email = table.Column<string>(type: "text", nullable: false),
                    Telephone = table.Column<string>(type: "text", nullable: true),
                    PasswordHash = table.Column<string>(type: "text", nullable: true),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    Description = table.Column<string>(type: "text", nullable: true),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    Admin = table.Column<bool>(type: "boolean", nullable: false),
                    Premium = table.Column<bool>(type: "boolean", nullable: false),
                    VerifiedTrail = table.Column<bool>(type: "boolean", nullable: false),
                    SubscriptionEndDate = table.Column<DateTime>(type: "timestamp with time zone", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    Dob = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "UserSettings",
                schema: "public",
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

            migrationBuilder.CreateTable(
                name: "EntityComments",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    Comment = table.Column<string>(type: "text", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    ParentCommentId = table.Column<Guid>(type: "uuid", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityComments", x => x.Id);
                    table.ForeignKey(
                        name: "FK_EntityComments_EntityComments_ParentCommentId",
                        column: x => x.ParentCommentId,
                        principalSchema: "public",
                        principalTable: "EntityComments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_EntityComments_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "EntityReactions",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    Reaction = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityReactions", x => x.Id);
                    table.ForeignKey(
                        name: "FK_EntityReactions_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Horses",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Breed = table.Column<string>(type: "text", nullable: true),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    Dob = table.Column<DateTime>(type: "timestamp with time zone", nullable: true),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Horses", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Horses_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Messages",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    SUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    RUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    MessageText = table.Column<string>(type: "text", nullable: false),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Messages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Messages_Users_RUserId",
                        column: x => x.RUserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Messages_Users_SUserId",
                        column: x => x.SUserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "StableMessages",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    StableId = table.Column<Guid>(type: "uuid", nullable: false),
                    MessageText = table.Column<string>(type: "text", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    SentAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StableMessages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_StableMessages_Stables_StableId",
                        column: x => x.StableId,
                        principalSchema: "public",
                        principalTable: "Stables",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StableMessages_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "StableUsers",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    StableId = table.Column<Guid>(type: "uuid", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
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
                        principalSchema: "public",
                        principalTable: "Stables",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StableUsers_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "SubscriptionOrders",
                schema: "public",
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
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Trails",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: true),
                    Distance = table.Column<double>(type: "double precision", nullable: false),
                    LatMean = table.Column<double>(type: "double precision", nullable: false),
                    LongMean = table.Column<double>(type: "double precision", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: true),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Trails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Trails_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "UserRelations",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    FromUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    ToUserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Status = table.Column<string>(type: "text", nullable: false),
                    LikesCount = table.Column<int>(type: "integer", nullable: false),
                    CommentsCount = table.Column<int>(type: "integer", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserRelations", x => x.Id);
                    table.ForeignKey(
                        name: "FK_UserRelations_Users_FromUserId",
                        column: x => x.FromUserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_UserRelations_Users_ToUserId",
                        column: x => x.ToUserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "UserReports",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    Message = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserReports", x => x.Id);
                    table.ForeignKey(
                        name: "FK_UserReports_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailAllCoordinates",
                schema: "public",
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
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailDetails",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Description = table.Column<string>(type: "text", nullable: true),
                    LatMin = table.Column<double>(type: "double precision", nullable: true),
                    LongMin = table.Column<double>(type: "double precision", nullable: true),
                    LatMax = table.Column<double>(type: "double precision", nullable: true),
                    LongMax = table.Column<double>(type: "double precision", nullable: true),
                    JsonCoordinates50 = table.Column<string>(type: "text", nullable: true),
                    Notes = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailDetails_Trails_Id",
                        column: x => x.Id,
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailFilters",
                schema: "public",
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
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailFilterValues",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: false),
                    FilterDefinitionId = table.Column<Guid>(type: "uuid", nullable: false),
                    Value = table.Column<string>(type: "text", nullable: false),
                    TrailFilterDefinitionId = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailFilterValues", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailFilterValues_TrailFilterDefinitions_TrailFilterDefinit~",
                        column: x => x.TrailFilterDefinitionId,
                        principalSchema: "public",
                        principalTable: "TrailFilterDefinitions",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TrailFilterValues_Trails_TrailId",
                        column: x => x.TrailId,
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailRatings",
                schema: "public",
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
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TrailRatings_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TrailReviews",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Rating = table.Column<int>(type: "integer", nullable: false),
                    Comment = table.Column<string>(type: "text", nullable: false),
                    PictureUrl = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TrailReviews", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TrailReviews_Trails_TrailId",
                        column: x => x.TrailId,
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TrailReviews_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "UserHikes",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    Length = table.Column<double>(type: "double precision", nullable: true),
                    Duration = table.Column<double>(type: "double precision", nullable: false),
                    HorseId = table.Column<Guid>(type: "uuid", nullable: true),
                    TrailId = table.Column<Guid>(type: "uuid", nullable: true),
                    PictureUrl = table.Column<string>(type: "text", nullable: true),
                    Comment = table.Column<string>(type: "text", nullable: true),
                    Secret = table.Column<bool>(type: "boolean", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserHikes", x => x.Id);
                    table.ForeignKey(
                        name: "FK_UserHikes_Horses_HorseId",
                        column: x => x.HorseId,
                        principalSchema: "public",
                        principalTable: "Horses",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_UserHikes_Trails_TrailId",
                        column: x => x.TrailId,
                        principalSchema: "public",
                        principalTable: "Trails",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_UserHikes_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

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
                name: "EntityImages",
                schema: "public",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    ImageId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityId = table.Column<Guid>(type: "uuid", nullable: false),
                    EntityName = table.Column<string>(type: "text", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    TrailDetailId = table.Column<Guid>(type: "uuid", nullable: true),
                    UserId = table.Column<Guid>(type: "uuid", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EntityImages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_EntityImages_Images_ImageId",
                        column: x => x.ImageId,
                        principalSchema: "public",
                        principalTable: "Images",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_EntityImages_TrailDetails_TrailDetailId",
                        column: x => x.TrailDetailId,
                        principalSchema: "public",
                        principalTable: "TrailDetails",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_EntityImages_Users_UserId",
                        column: x => x.UserId,
                        principalSchema: "public",
                        principalTable: "Users",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateIndex(
                name: "IX_EntityComments_ParentCommentId",
                schema: "public",
                table: "EntityComments",
                column: "ParentCommentId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityComments_UserId",
                schema: "public",
                table: "EntityComments",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_ImageId",
                schema: "public",
                table: "EntityImages",
                column: "ImageId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_TrailDetailId",
                schema: "public",
                table: "EntityImages",
                column: "TrailDetailId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityImages_UserId",
                schema: "public",
                table: "EntityImages",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityReactions_UserId_EntityId_EntityName",
                schema: "public",
                table: "EntityReactions",
                columns: new[] { "UserId", "EntityId", "EntityName" },
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_Horses_UserId",
                schema: "public",
                table: "Horses",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Messages_RUserId",
                schema: "public",
                table: "Messages",
                column: "RUserId");

            migrationBuilder.CreateIndex(
                name: "IX_Messages_SUserId",
                schema: "public",
                table: "Messages",
                column: "SUserId");

            migrationBuilder.CreateIndex(
                name: "IX_StableMessages_StableId",
                schema: "public",
                table: "StableMessages",
                column: "StableId");

            migrationBuilder.CreateIndex(
                name: "IX_StableMessages_UserId",
                schema: "public",
                table: "StableMessages",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_StableUsers_StableId",
                schema: "public",
                table: "StableUsers",
                column: "StableId");

            migrationBuilder.CreateIndex(
                name: "IX_StableUsers_UserId",
                schema: "public",
                table: "StableUsers",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_SubscriptionOrders_UserId",
                schema: "public",
                table: "SubscriptionOrders",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailCoordinate_TrailAllCoordinatesId",
                schema: "public",
                table: "TrailCoordinate",
                column: "TrailAllCoordinatesId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailFilterValues_TrailFilterDefinitionId",
                schema: "public",
                table: "TrailFilterValues",
                column: "TrailFilterDefinitionId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailFilterValues_TrailId",
                schema: "public",
                table: "TrailFilterValues",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailRatings_TrailId",
                schema: "public",
                table: "TrailRatings",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailRatings_UserId",
                schema: "public",
                table: "TrailRatings",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailReviews_TrailId",
                schema: "public",
                table: "TrailReviews",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_TrailReviews_UserId",
                schema: "public",
                table: "TrailReviews",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Trails_UserId",
                schema: "public",
                table: "Trails",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_UserHikes_HorseId",
                schema: "public",
                table: "UserHikes",
                column: "HorseId");

            migrationBuilder.CreateIndex(
                name: "IX_UserHikes_TrailId",
                schema: "public",
                table: "UserHikes",
                column: "TrailId");

            migrationBuilder.CreateIndex(
                name: "IX_UserHikes_UserId",
                schema: "public",
                table: "UserHikes",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_UserRelations_FromUserId",
                schema: "public",
                table: "UserRelations",
                column: "FromUserId");

            migrationBuilder.CreateIndex(
                name: "IX_UserRelations_ToUserId",
                schema: "public",
                table: "UserRelations",
                column: "ToUserId");

            migrationBuilder.CreateIndex(
                name: "IX_UserReports_UserId",
                schema: "public",
                table: "UserReports",
                column: "UserId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "EntityComments",
                schema: "public");

            migrationBuilder.DropTable(
                name: "EntityFeeds",
                schema: "public");

            migrationBuilder.DropTable(
                name: "EntityImages",
                schema: "public");

            migrationBuilder.DropTable(
                name: "EntityReactions",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Messages",
                schema: "public");

            migrationBuilder.DropTable(
                name: "StableMessages",
                schema: "public");

            migrationBuilder.DropTable(
                name: "StableUsers",
                schema: "public");

            migrationBuilder.DropTable(
                name: "SubscriptionOrders",
                schema: "public");

            migrationBuilder.DropTable(
                name: "SystemSettings",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailCoordinate",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailFilters",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailFilterValues",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailRatings",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailReviews",
                schema: "public");

            migrationBuilder.DropTable(
                name: "UserHikes",
                schema: "public");

            migrationBuilder.DropTable(
                name: "UserRelations",
                schema: "public");

            migrationBuilder.DropTable(
                name: "UserReports",
                schema: "public");

            migrationBuilder.DropTable(
                name: "UserSettings",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Images",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailDetails",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Stables",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailAllCoordinates",
                schema: "public");

            migrationBuilder.DropTable(
                name: "TrailFilterDefinitions",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Horses",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Trails",
                schema: "public");

            migrationBuilder.DropTable(
                name: "Users",
                schema: "public");
        }
    }
}
