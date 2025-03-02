using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HoplaBackend.Migrations
{
    /// <inheritdoc />
    public partial class UpdateDatabaseSchema : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_Images_ImageId",
                table: "EntityImages");

            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages");

            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_TrailDetails_TrailDetailsId",
                table: "EntityImages");

            migrationBuilder.DropColumn(
                name: "PictureUrl",
                table: "TrailDetails");

            migrationBuilder.DropColumn(
                name: "ThumbnailUrl",
                table: "Images");

            migrationBuilder.RenameColumn(
                name: "ProfilePictureUrl",
                table: "Users",
                newName: "Telephone");

            migrationBuilder.RenameColumn(
                name: "ReviewText",
                table: "TrailReview",
                newName: "PictureUrl");

            migrationBuilder.RenameColumn(
                name: "SentAt",
                table: "Messages",
                newName: "CreatedAt");

            migrationBuilder.RenameColumn(
                name: "Url",
                table: "Images",
                newName: "PictureUrl");

            migrationBuilder.RenameColumn(
                name: "HorsePictureUrl",
                table: "Horses",
                newName: "PictureUrl");

            migrationBuilder.RenameColumn(
                name: "TrailDetailsId",
                table: "EntityImages",
                newName: "TrailDetailId");

            migrationBuilder.RenameIndex(
                name: "IX_EntityImages_TrailDetailsId",
                table: "EntityImages",
                newName: "IX_EntityImages_TrailDetailId");

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "Users",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "Users",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "PictureUrl",
                table: "Users",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "UserRelations",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "UserRelations",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "Trails",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "Trails",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "PictureUrl",
                table: "Trails",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Comment",
                table: "TrailReview",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "StableUsers",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "StableUsers",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "Stables",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "Stables",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "StableMessages",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "StableMessages",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "PictureUrl",
                table: "Messages",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "Images",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "Images",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "Horses",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "Horses",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AlterColumn<Guid>(
                name: "ImageId",
                table: "EntityImages",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"),
                oldClrType: typeof(Guid),
                oldType: "uuid",
                oldNullable: true);

            migrationBuilder.AddColumn<Guid>(
                name: "EntityId",
                table: "EntityImages",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<string>(
                name: "EntityName",
                table: "EntityImages",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<int>(
                name: "CommentsCount",
                table: "EntityFeeds",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "EntityObject",
                table: "EntityFeeds",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "EntityTitle",
                table: "EntityFeeds",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<int>(
                name: "LikesCount",
                table: "EntityFeeds",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateTable(
                name: "EntityComments",
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
                        principalTable: "EntityComments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_EntityComments_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "EntityReactions",
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
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "UserReports",
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
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_EntityComments_ParentCommentId",
                table: "EntityComments",
                column: "ParentCommentId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityComments_UserId",
                table: "EntityComments",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_EntityReactions_UserId_EntityId_EntityName",
                table: "EntityReactions",
                columns: new[] { "UserId", "EntityId", "EntityName" },
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_UserReports_UserId",
                table: "UserReports",
                column: "UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_Images_ImageId",
                table: "EntityImages",
                column: "ImageId",
                principalTable: "Images",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages",
                column: "RideDetailId",
                principalTable: "RideDetails",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_TrailDetails_TrailDetailId",
                table: "EntityImages",
                column: "TrailDetailId",
                principalTable: "TrailDetails",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_Images_ImageId",
                table: "EntityImages");

            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_RideDetails_RideDetailId",
                table: "EntityImages");

            migrationBuilder.DropForeignKey(
                name: "FK_EntityImages_TrailDetails_TrailDetailId",
                table: "EntityImages");

            migrationBuilder.DropTable(
                name: "EntityComments");

            migrationBuilder.DropTable(
                name: "EntityReactions");

            migrationBuilder.DropTable(
                name: "UserReports");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "PictureUrl",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "UserRelations");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "UserRelations");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "PictureUrl",
                table: "Trails");

            migrationBuilder.DropColumn(
                name: "Comment",
                table: "TrailReview");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "StableUsers");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "StableUsers");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "Stables");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "Stables");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "StableMessages");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "StableMessages");

            migrationBuilder.DropColumn(
                name: "PictureUrl",
                table: "Messages");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "Images");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "Images");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "Horses");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "Horses");

            migrationBuilder.DropColumn(
                name: "EntityId",
                table: "EntityImages");

            migrationBuilder.DropColumn(
                name: "EntityName",
                table: "EntityImages");

            migrationBuilder.DropColumn(
                name: "CommentsCount",
                table: "EntityFeeds");

            migrationBuilder.DropColumn(
                name: "EntityObject",
                table: "EntityFeeds");

            migrationBuilder.DropColumn(
                name: "EntityTitle",
                table: "EntityFeeds");

            migrationBuilder.DropColumn(
                name: "LikesCount",
                table: "EntityFeeds");

            migrationBuilder.RenameColumn(
                name: "Telephone",
                table: "Users",
                newName: "ProfilePictureUrl");

            migrationBuilder.RenameColumn(
                name: "PictureUrl",
                table: "TrailReview",
                newName: "ReviewText");

            migrationBuilder.RenameColumn(
                name: "CreatedAt",
                table: "Messages",
                newName: "SentAt");

            migrationBuilder.RenameColumn(
                name: "PictureUrl",
                table: "Images",
                newName: "Url");

            migrationBuilder.RenameColumn(
                name: "PictureUrl",
                table: "Horses",
                newName: "HorsePictureUrl");

            migrationBuilder.RenameColumn(
                name: "TrailDetailId",
                table: "EntityImages",
                newName: "TrailDetailsId");

            migrationBuilder.RenameIndex(
                name: "IX_EntityImages_TrailDetailId",
                table: "EntityImages",
                newName: "IX_EntityImages_TrailDetailsId");

            migrationBuilder.AddColumn<string>(
                name: "PictureUrl",
                table: "TrailDetails",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "ThumbnailUrl",
                table: "Images",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AlterColumn<Guid>(
                name: "ImageId",
                table: "EntityImages",
                type: "uuid",
                nullable: true,
                oldClrType: typeof(Guid),
                oldType: "uuid");

            migrationBuilder.AddForeignKey(
                name: "FK_EntityImages_Images_ImageId",
                table: "EntityImages",
                column: "ImageId",
                principalTable: "Images",
                principalColumn: "Id");

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
        }
    }
}
