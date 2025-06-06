PGDMP                      }            hopla3    17.2    17.2 �    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    17115    hopla3    DATABASE     r   CREATE DATABASE hopla3 WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'nb_NO.UTF-8';
    DROP DATABASE hopla3;
                     postgres    false                        2615    28393    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                     postgres    false            �           0    0    SCHEMA public    COMMENT         COMMENT ON SCHEMA public IS '';
                        postgres    false    5            �           0    0    SCHEMA public    ACL     +   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
                        postgres    false    5            �            1259    28499    EntityImages    TABLE     �   CREATE TABLE public."EntityImages" (
    "Id" uuid NOT NULL,
    "ImageId" uuid,
    "RideDetailId" uuid,
    "TrailDetailsId" uuid,
    "CreatedAt" timestamp with time zone NOT NULL,
    "UserId" uuid
);
 "   DROP TABLE public."EntityImages";
       public         heap r       postgres    false    5            �            1259    28421    Horses    TABLE     �   CREATE TABLE public."Horses" (
    "Id" uuid NOT NULL,
    "Name" text NOT NULL,
    "UserId" uuid NOT NULL,
    "Breed" text,
    "Dob" timestamp with time zone,
    "CreatedAt" timestamp with time zone
);
    DROP TABLE public."Horses";
       public         heap r       postgres    false    5            �            1259    28400    Images    TABLE     �   CREATE TABLE public."Images" (
    "Id" uuid NOT NULL,
    "Url" text NOT NULL,
    "ThumbnailUrl" text NOT NULL,
    "Description" text NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL
);
    DROP TABLE public."Images";
       public         heap r       postgres    false    5            �            1259    28433    Messages    TABLE     �   CREATE TABLE public."Messages" (
    "Id" uuid NOT NULL,
    "SUserId" uuid NOT NULL,
    "RUserId" uuid NOT NULL,
    "MessageText" text NOT NULL,
    "SentAt" timestamp with time zone
);
    DROP TABLE public."Messages";
       public         heap r       postgres    false    5            �            1259    28514    RideDetails    TABLE     6  CREATE TABLE public."RideDetails" (
    "Id" uuid NOT NULL,
    "LatMean" double precision,
    "LongMean" double precision,
    "LatMin" double precision,
    "LongMin" double precision,
    "LatMax" double precision,
    "LongMax" double precision,
    "JsonCoordinates50" text,
    "Notes" text NOT NULL
);
 !   DROP TABLE public."RideDetails";
       public         heap r       postgres    false    5            �            1259    28521    RideReviews    TABLE     �   CREATE TABLE public."RideReviews" (
    "Id" uuid NOT NULL,
    "UserId" uuid NOT NULL,
    "Rating" integer NOT NULL,
    "ReviewText" text NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL
);
 !   DROP TABLE public."RideReviews";
       public         heap r       postgres    false    5            �            1259    28548    RideTrackingDatas    TABLE     h   CREATE TABLE public."RideTrackingDatas" (
    "Id" uuid NOT NULL,
    "TrackingPoints" json NOT NULL
);
 '   DROP TABLE public."RideTrackingDatas";
       public         heap r       postgres    false    5            �            1259    28533    Rides    TABLE     �   CREATE TABLE public."Rides" (
    "Id" uuid NOT NULL,
    "Length" double precision NOT NULL,
    "Duration" double precision NOT NULL,
    "UserId" uuid,
    "HorseId" uuid,
    "TrailId" uuid,
    "CreatedAt" timestamp with time zone NOT NULL
);
    DROP TABLE public."Rides";
       public         heap r       postgres    false    5            �            1259    28450    StableMessages    TABLE     �   CREATE TABLE public."StableMessages" (
    "Id" uuid NOT NULL,
    "UserId" uuid NOT NULL,
    "StableId" uuid NOT NULL,
    "MessageText" text NOT NULL,
    "SentAt" timestamp with time zone NOT NULL
);
 $   DROP TABLE public."StableMessages";
       public         heap r       postgres    false    5            �            1259    28467    StableUsers    TABLE     8  CREATE TABLE public."StableUsers" (
    "Id" uuid NOT NULL,
    "UserId" uuid NOT NULL,
    "StableId" uuid NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL,
    "IsOwner" boolean NOT NULL,
    "IsAdmin" boolean NOT NULL,
    "IsModerator" boolean NOT NULL,
    "NotifyNewMessage" boolean NOT NULL
);
 !   DROP TABLE public."StableUsers";
       public         heap r       postgres    false    5            �            1259    28407    Stables    TABLE       CREATE TABLE public."Stables" (
    "Id" uuid NOT NULL,
    "Name" text NOT NULL,
    "Location" text,
    "PrivateGroup" boolean NOT NULL,
    "ModeratedMessages" boolean NOT NULL,
    "SecretGroup" boolean NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL
);
    DROP TABLE public."Stables";
       public         heap r       postgres    false    5            �            1259    28695    SystemSettings    TABLE     �   CREATE TABLE public."SystemSettings" (
    "Id" integer NOT NULL,
    "Key" text NOT NULL,
    "Value" text NOT NULL,
    "Type" text DEFAULT ''::text NOT NULL
);
 $   DROP TABLE public."SystemSettings";
       public         heap r       postgres    false    5            �            1259    28694    SystemSettings_Id_seq    SEQUENCE     �   ALTER TABLE public."SystemSettings" ALTER COLUMN "Id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public."SystemSettings_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    5    241            �            1259    28573    TrackingPoints    TABLE     �   CREATE TABLE public."TrackingPoints" (
    "Id" integer NOT NULL,
    "RideTrackingDataId" uuid NOT NULL,
    "Lat" double precision NOT NULL,
    "Long" double precision NOT NULL,
    "TimeSinceLast" double precision
);
 $   DROP TABLE public."TrackingPoints";
       public         heap r       postgres    false    5            �            1259    28572    TrackingPoints_Id_seq    SEQUENCE     �   ALTER TABLE public."TrackingPoints" ALTER COLUMN "Id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public."TrackingPoints_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    5    233            �            1259    28583    TrailAllCoordinates    TABLE     F   CREATE TABLE public."TrailAllCoordinates" (
    "Id" uuid NOT NULL
);
 )   DROP TABLE public."TrailAllCoordinates";
       public         heap r       postgres    false    5            �            1259    28635    TrailCoordinate    TABLE     �   CREATE TABLE public."TrailCoordinate" (
    "Id" integer NOT NULL,
    "TrailAllCoordinatesId" uuid NOT NULL,
    "Lat" double precision NOT NULL,
    "Long" double precision NOT NULL
);
 %   DROP TABLE public."TrailCoordinate";
       public         heap r       postgres    false    5            �            1259    28634    TrailCoordinate_Id_seq    SEQUENCE     �   ALTER TABLE public."TrailCoordinate" ALTER COLUMN "Id" ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public."TrailCoordinate_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public               postgres    false    239    5            �            1259    28593    TrailDetails    TABLE     E  CREATE TABLE public."TrailDetails" (
    "Id" uuid NOT NULL,
    "PictureThumbURL" text,
    "PictureFullURL" text,
    "LatMin" double precision,
    "LongMin" double precision,
    "LatMax" double precision,
    "LongMax" double precision,
    "JsonCoordinates50" text,
    "Description" text,
    "Notes" text NOT NULL
);
 "   DROP TABLE public."TrailDetails";
       public         heap r       postgres    false    5            �            1259    28605    TrailFilters    TABLE     (  CREATE TABLE public."TrailFilters" (
    "Id" uuid NOT NULL,
    "Length" double precision NOT NULL,
    "HasBridge" boolean NOT NULL,
    "Season" text NOT NULL,
    "Cart" boolean NOT NULL,
    "TrafficRoads" boolean NOT NULL,
    "PeopleTraffic" boolean NOT NULL,
    "Other" text NOT NULL
);
 "   DROP TABLE public."TrailFilters";
       public         heap r       postgres    false    5            �            1259    28617    TrailReview    TABLE     �   CREATE TABLE public."TrailReview" (
    "Id" uuid NOT NULL,
    "TrailId" uuid NOT NULL,
    "UserId" uuid NOT NULL,
    "Rating" integer NOT NULL,
    "ReviewText" text NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL
);
 !   DROP TABLE public."TrailReview";
       public         heap r       postgres    false    5            �            1259    28560    Trails    TABLE     �   CREATE TABLE public."Trails" (
    "Id" uuid NOT NULL,
    "Name" text,
    "LatMean" double precision NOT NULL,
    "LongMean" double precision NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL,
    "RideId" uuid NOT NULL
);
    DROP TABLE public."Trails";
       public         heap r       postgres    false    5            �            1259    28482    UserRelations    TABLE     �   CREATE TABLE public."UserRelations" (
    "Id" uuid NOT NULL,
    "FromUserId" uuid NOT NULL,
    "ToUserId" uuid NOT NULL,
    "Status" text NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL
);
 #   DROP TABLE public."UserRelations";
       public         heap r       postgres    false    5            �            1259    28414    Users    TABLE     �  CREATE TABLE public."Users" (
    "Id" uuid NOT NULL,
    "Name" text,
    "Alias" text,
    "Email" text DEFAULT ''::text NOT NULL,
    "PasswordHash" text,
    "ProfilePictureUrl" text,
    "Admin" boolean NOT NULL,
    "Premium" boolean NOT NULL,
    "VerifiedTrail" boolean NOT NULL,
    "CreatedAt" timestamp with time zone NOT NULL,
    "Dob" timestamp with time zone,
    "Description" text
);
    DROP TABLE public."Users";
       public         heap r       postgres    false    5            �            1259    28395    __EFMigrationsHistory    TABLE     �   CREATE TABLE public."__EFMigrationsHistory" (
    "MigrationId" character varying(150) NOT NULL,
    "ProductVersion" character varying(32) NOT NULL
);
 +   DROP TABLE public."__EFMigrationsHistory";
       public         heap r       postgres    false    5            �          0    28499    EntityImages 
   TABLE DATA           r   COPY public."EntityImages" ("Id", "ImageId", "RideDetailId", "TrailDetailsId", "CreatedAt", "UserId") FROM stdin;
    public               postgres    false    226   �       �          0    28421    Horses 
   TABLE DATA           W   COPY public."Horses" ("Id", "Name", "UserId", "Breed", "Dob", "CreatedAt") FROM stdin;
    public               postgres    false    221   �       �          0    28400    Images 
   TABLE DATA           [   COPY public."Images" ("Id", "Url", "ThumbnailUrl", "Description", "CreatedAt") FROM stdin;
    public               postgres    false    218   4�       �          0    28433    Messages 
   TABLE DATA           Y   COPY public."Messages" ("Id", "SUserId", "RUserId", "MessageText", "SentAt") FROM stdin;
    public               postgres    false    222   Q�       �          0    28514    RideDetails 
   TABLE DATA           �   COPY public."RideDetails" ("Id", "LatMean", "LongMean", "LatMin", "LongMin", "LatMax", "LongMax", "JsonCoordinates50", "Notes") FROM stdin;
    public               postgres    false    227   _�       �          0    28521    RideReviews 
   TABLE DATA           \   COPY public."RideReviews" ("Id", "UserId", "Rating", "ReviewText", "CreatedAt") FROM stdin;
    public               postgres    false    228   a�       �          0    28548    RideTrackingDatas 
   TABLE DATA           E   COPY public."RideTrackingDatas" ("Id", "TrackingPoints") FROM stdin;
    public               postgres    false    230   ~�       �          0    28533    Rides 
   TABLE DATA           j   COPY public."Rides" ("Id", "Length", "Duration", "UserId", "HorseId", "TrailId", "CreatedAt") FROM stdin;
    public               postgres    false    229   ��       �          0    28450    StableMessages 
   TABLE DATA           _   COPY public."StableMessages" ("Id", "UserId", "StableId", "MessageText", "SentAt") FROM stdin;
    public               postgres    false    223   �       �          0    28467    StableUsers 
   TABLE DATA           �   COPY public."StableUsers" ("Id", "UserId", "StableId", "CreatedAt", "IsOwner", "IsAdmin", "IsModerator", "NotifyNewMessage") FROM stdin;
    public               postgres    false    224   �       �          0    28407    Stables 
   TABLE DATA           ~   COPY public."Stables" ("Id", "Name", "Location", "PrivateGroup", "ModeratedMessages", "SecretGroup", "CreatedAt") FROM stdin;
    public               postgres    false    219   -�       �          0    28695    SystemSettings 
   TABLE DATA           H   COPY public."SystemSettings" ("Id", "Key", "Value", "Type") FROM stdin;
    public               postgres    false    241   �       �          0    28573    TrackingPoints 
   TABLE DATA           f   COPY public."TrackingPoints" ("Id", "RideTrackingDataId", "Lat", "Long", "TimeSinceLast") FROM stdin;
    public               postgres    false    233   ��       �          0    28583    TrailAllCoordinates 
   TABLE DATA           5   COPY public."TrailAllCoordinates" ("Id") FROM stdin;
    public               postgres    false    234   ��       �          0    28635    TrailCoordinate 
   TABLE DATA           Y   COPY public."TrailCoordinate" ("Id", "TrailAllCoordinatesId", "Lat", "Long") FROM stdin;
    public               postgres    false    239   ��       �          0    28593    TrailDetails 
   TABLE DATA           �   COPY public."TrailDetails" ("Id", "PictureThumbURL", "PictureFullURL", "LatMin", "LongMin", "LatMax", "LongMax", "JsonCoordinates50", "Description", "Notes") FROM stdin;
    public               postgres    false    235   ��       �          0    28605    TrailFilters 
   TABLE DATA           �   COPY public."TrailFilters" ("Id", "Length", "HasBridge", "Season", "Cart", "TrafficRoads", "PeopleTraffic", "Other") FROM stdin;
    public               postgres    false    236   ��       �          0    28617    TrailReview 
   TABLE DATA           g   COPY public."TrailReview" ("Id", "TrailId", "UserId", "Rating", "ReviewText", "CreatedAt") FROM stdin;
    public               postgres    false    237   ��       �          0    28560    Trails 
   TABLE DATA           ^   COPY public."Trails" ("Id", "Name", "LatMean", "LongMean", "CreatedAt", "RideId") FROM stdin;
    public               postgres    false    231   ��       �          0    28482    UserRelations 
   TABLE DATA           `   COPY public."UserRelations" ("Id", "FromUserId", "ToUserId", "Status", "CreatedAt") FROM stdin;
    public               postgres    false    225   ��       �          0    28414    Users 
   TABLE DATA           �   COPY public."Users" ("Id", "Name", "Alias", "Email", "PasswordHash", "ProfilePictureUrl", "Admin", "Premium", "VerifiedTrail", "CreatedAt", "Dob", "Description") FROM stdin;
    public               postgres    false    220   c�       �          0    28395    __EFMigrationsHistory 
   TABLE DATA           R   COPY public."__EFMigrationsHistory" ("MigrationId", "ProductVersion") FROM stdin;
    public               postgres    false    217   ��       �           0    0    SystemSettings_Id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public."SystemSettings_Id_seq"', 23, true);
          public               postgres    false    240            �           0    0    TrackingPoints_Id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public."TrackingPoints_Id_seq"', 1, false);
          public               postgres    false    232            �           0    0    TrailCoordinate_Id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('public."TrailCoordinate_Id_seq"', 1, false);
          public               postgres    false    238            �           2606    28503    EntityImages PK_EntityImages 
   CONSTRAINT     `   ALTER TABLE ONLY public."EntityImages"
    ADD CONSTRAINT "PK_EntityImages" PRIMARY KEY ("Id");
 J   ALTER TABLE ONLY public."EntityImages" DROP CONSTRAINT "PK_EntityImages";
       public                 postgres    false    226            �           2606    28427    Horses PK_Horses 
   CONSTRAINT     T   ALTER TABLE ONLY public."Horses"
    ADD CONSTRAINT "PK_Horses" PRIMARY KEY ("Id");
 >   ALTER TABLE ONLY public."Horses" DROP CONSTRAINT "PK_Horses";
       public                 postgres    false    221            �           2606    28406    Images PK_Images 
   CONSTRAINT     T   ALTER TABLE ONLY public."Images"
    ADD CONSTRAINT "PK_Images" PRIMARY KEY ("Id");
 >   ALTER TABLE ONLY public."Images" DROP CONSTRAINT "PK_Images";
       public                 postgres    false    218            �           2606    28439    Messages PK_Messages 
   CONSTRAINT     X   ALTER TABLE ONLY public."Messages"
    ADD CONSTRAINT "PK_Messages" PRIMARY KEY ("Id");
 B   ALTER TABLE ONLY public."Messages" DROP CONSTRAINT "PK_Messages";
       public                 postgres    false    222            �           2606    28520    RideDetails PK_RideDetails 
   CONSTRAINT     ^   ALTER TABLE ONLY public."RideDetails"
    ADD CONSTRAINT "PK_RideDetails" PRIMARY KEY ("Id");
 H   ALTER TABLE ONLY public."RideDetails" DROP CONSTRAINT "PK_RideDetails";
       public                 postgres    false    227            �           2606    28527    RideReviews PK_RideReviews 
   CONSTRAINT     ^   ALTER TABLE ONLY public."RideReviews"
    ADD CONSTRAINT "PK_RideReviews" PRIMARY KEY ("Id");
 H   ALTER TABLE ONLY public."RideReviews" DROP CONSTRAINT "PK_RideReviews";
       public                 postgres    false    228            �           2606    28554 &   RideTrackingDatas PK_RideTrackingDatas 
   CONSTRAINT     j   ALTER TABLE ONLY public."RideTrackingDatas"
    ADD CONSTRAINT "PK_RideTrackingDatas" PRIMARY KEY ("Id");
 T   ALTER TABLE ONLY public."RideTrackingDatas" DROP CONSTRAINT "PK_RideTrackingDatas";
       public                 postgres    false    230            �           2606    28537    Rides PK_Rides 
   CONSTRAINT     R   ALTER TABLE ONLY public."Rides"
    ADD CONSTRAINT "PK_Rides" PRIMARY KEY ("Id");
 <   ALTER TABLE ONLY public."Rides" DROP CONSTRAINT "PK_Rides";
       public                 postgres    false    229            �           2606    28456     StableMessages PK_StableMessages 
   CONSTRAINT     d   ALTER TABLE ONLY public."StableMessages"
    ADD CONSTRAINT "PK_StableMessages" PRIMARY KEY ("Id");
 N   ALTER TABLE ONLY public."StableMessages" DROP CONSTRAINT "PK_StableMessages";
       public                 postgres    false    223            �           2606    28471    StableUsers PK_StableUsers 
   CONSTRAINT     ^   ALTER TABLE ONLY public."StableUsers"
    ADD CONSTRAINT "PK_StableUsers" PRIMARY KEY ("Id");
 H   ALTER TABLE ONLY public."StableUsers" DROP CONSTRAINT "PK_StableUsers";
       public                 postgres    false    224            �           2606    28413    Stables PK_Stables 
   CONSTRAINT     V   ALTER TABLE ONLY public."Stables"
    ADD CONSTRAINT "PK_Stables" PRIMARY KEY ("Id");
 @   ALTER TABLE ONLY public."Stables" DROP CONSTRAINT "PK_Stables";
       public                 postgres    false    219            �           2606    28701     SystemSettings PK_SystemSettings 
   CONSTRAINT     d   ALTER TABLE ONLY public."SystemSettings"
    ADD CONSTRAINT "PK_SystemSettings" PRIMARY KEY ("Id");
 N   ALTER TABLE ONLY public."SystemSettings" DROP CONSTRAINT "PK_SystemSettings";
       public                 postgres    false    241            �           2606    28577     TrackingPoints PK_TrackingPoints 
   CONSTRAINT     d   ALTER TABLE ONLY public."TrackingPoints"
    ADD CONSTRAINT "PK_TrackingPoints" PRIMARY KEY ("Id");
 N   ALTER TABLE ONLY public."TrackingPoints" DROP CONSTRAINT "PK_TrackingPoints";
       public                 postgres    false    233            �           2606    28587 *   TrailAllCoordinates PK_TrailAllCoordinates 
   CONSTRAINT     n   ALTER TABLE ONLY public."TrailAllCoordinates"
    ADD CONSTRAINT "PK_TrailAllCoordinates" PRIMARY KEY ("Id");
 X   ALTER TABLE ONLY public."TrailAllCoordinates" DROP CONSTRAINT "PK_TrailAllCoordinates";
       public                 postgres    false    234            �           2606    28639 "   TrailCoordinate PK_TrailCoordinate 
   CONSTRAINT     f   ALTER TABLE ONLY public."TrailCoordinate"
    ADD CONSTRAINT "PK_TrailCoordinate" PRIMARY KEY ("Id");
 P   ALTER TABLE ONLY public."TrailCoordinate" DROP CONSTRAINT "PK_TrailCoordinate";
       public                 postgres    false    239            �           2606    28599    TrailDetails PK_TrailDetails 
   CONSTRAINT     `   ALTER TABLE ONLY public."TrailDetails"
    ADD CONSTRAINT "PK_TrailDetails" PRIMARY KEY ("Id");
 J   ALTER TABLE ONLY public."TrailDetails" DROP CONSTRAINT "PK_TrailDetails";
       public                 postgres    false    235            �           2606    28611    TrailFilters PK_TrailFilters 
   CONSTRAINT     `   ALTER TABLE ONLY public."TrailFilters"
    ADD CONSTRAINT "PK_TrailFilters" PRIMARY KEY ("Id");
 J   ALTER TABLE ONLY public."TrailFilters" DROP CONSTRAINT "PK_TrailFilters";
       public                 postgres    false    236            �           2606    28623    TrailReview PK_TrailReview 
   CONSTRAINT     ^   ALTER TABLE ONLY public."TrailReview"
    ADD CONSTRAINT "PK_TrailReview" PRIMARY KEY ("Id");
 H   ALTER TABLE ONLY public."TrailReview" DROP CONSTRAINT "PK_TrailReview";
       public                 postgres    false    237            �           2606    28566    Trails PK_Trails 
   CONSTRAINT     T   ALTER TABLE ONLY public."Trails"
    ADD CONSTRAINT "PK_Trails" PRIMARY KEY ("Id");
 >   ALTER TABLE ONLY public."Trails" DROP CONSTRAINT "PK_Trails";
       public                 postgres    false    231            �           2606    28488    UserRelations PK_UserRelations 
   CONSTRAINT     b   ALTER TABLE ONLY public."UserRelations"
    ADD CONSTRAINT "PK_UserRelations" PRIMARY KEY ("Id");
 L   ALTER TABLE ONLY public."UserRelations" DROP CONSTRAINT "PK_UserRelations";
       public                 postgres    false    225            �           2606    28420    Users PK_Users 
   CONSTRAINT     R   ALTER TABLE ONLY public."Users"
    ADD CONSTRAINT "PK_Users" PRIMARY KEY ("Id");
 <   ALTER TABLE ONLY public."Users" DROP CONSTRAINT "PK_Users";
       public                 postgres    false    220            �           2606    28399 .   __EFMigrationsHistory PK___EFMigrationsHistory 
   CONSTRAINT     {   ALTER TABLE ONLY public."__EFMigrationsHistory"
    ADD CONSTRAINT "PK___EFMigrationsHistory" PRIMARY KEY ("MigrationId");
 \   ALTER TABLE ONLY public."__EFMigrationsHistory" DROP CONSTRAINT "PK___EFMigrationsHistory";
       public                 postgres    false    217            �           1259    28645    IX_EntityImages_ImageId    INDEX     Y   CREATE INDEX "IX_EntityImages_ImageId" ON public."EntityImages" USING btree ("ImageId");
 -   DROP INDEX public."IX_EntityImages_ImageId";
       public                 postgres    false    226            �           1259    28646    IX_EntityImages_RideDetailId    INDEX     c   CREATE INDEX "IX_EntityImages_RideDetailId" ON public."EntityImages" USING btree ("RideDetailId");
 2   DROP INDEX public."IX_EntityImages_RideDetailId";
       public                 postgres    false    226            �           1259    28647    IX_EntityImages_TrailDetailsId    INDEX     g   CREATE INDEX "IX_EntityImages_TrailDetailsId" ON public."EntityImages" USING btree ("TrailDetailsId");
 4   DROP INDEX public."IX_EntityImages_TrailDetailsId";
       public                 postgres    false    226            �           1259    28648    IX_EntityImages_UserId    INDEX     W   CREATE INDEX "IX_EntityImages_UserId" ON public."EntityImages" USING btree ("UserId");
 ,   DROP INDEX public."IX_EntityImages_UserId";
       public                 postgres    false    226            �           1259    28649    IX_Horses_UserId    INDEX     K   CREATE INDEX "IX_Horses_UserId" ON public."Horses" USING btree ("UserId");
 &   DROP INDEX public."IX_Horses_UserId";
       public                 postgres    false    221            �           1259    28650    IX_Messages_RUserId    INDEX     Q   CREATE INDEX "IX_Messages_RUserId" ON public."Messages" USING btree ("RUserId");
 )   DROP INDEX public."IX_Messages_RUserId";
       public                 postgres    false    222            �           1259    28651    IX_Messages_SUserId    INDEX     Q   CREATE INDEX "IX_Messages_SUserId" ON public."Messages" USING btree ("SUserId");
 )   DROP INDEX public."IX_Messages_SUserId";
       public                 postgres    false    222            �           1259    28652    IX_RideReviews_UserId    INDEX     U   CREATE INDEX "IX_RideReviews_UserId" ON public."RideReviews" USING btree ("UserId");
 +   DROP INDEX public."IX_RideReviews_UserId";
       public                 postgres    false    228            �           1259    28653    IX_Rides_HorseId    INDEX     K   CREATE INDEX "IX_Rides_HorseId" ON public."Rides" USING btree ("HorseId");
 &   DROP INDEX public."IX_Rides_HorseId";
       public                 postgres    false    229            �           1259    28654    IX_Rides_TrailId    INDEX     K   CREATE INDEX "IX_Rides_TrailId" ON public."Rides" USING btree ("TrailId");
 &   DROP INDEX public."IX_Rides_TrailId";
       public                 postgres    false    229            �           1259    28655    IX_Rides_UserId    INDEX     I   CREATE INDEX "IX_Rides_UserId" ON public."Rides" USING btree ("UserId");
 %   DROP INDEX public."IX_Rides_UserId";
       public                 postgres    false    229            �           1259    28656    IX_StableMessages_StableId    INDEX     _   CREATE INDEX "IX_StableMessages_StableId" ON public."StableMessages" USING btree ("StableId");
 0   DROP INDEX public."IX_StableMessages_StableId";
       public                 postgres    false    223            �           1259    28657    IX_StableMessages_UserId    INDEX     [   CREATE INDEX "IX_StableMessages_UserId" ON public."StableMessages" USING btree ("UserId");
 .   DROP INDEX public."IX_StableMessages_UserId";
       public                 postgres    false    223            �           1259    28658    IX_StableUsers_StableId    INDEX     Y   CREATE INDEX "IX_StableUsers_StableId" ON public."StableUsers" USING btree ("StableId");
 -   DROP INDEX public."IX_StableUsers_StableId";
       public                 postgres    false    224            �           1259    28659    IX_StableUsers_UserId    INDEX     U   CREATE INDEX "IX_StableUsers_UserId" ON public."StableUsers" USING btree ("UserId");
 +   DROP INDEX public."IX_StableUsers_UserId";
       public                 postgres    false    224            �           1259    28660 $   IX_TrackingPoints_RideTrackingDataId    INDEX     s   CREATE INDEX "IX_TrackingPoints_RideTrackingDataId" ON public."TrackingPoints" USING btree ("RideTrackingDataId");
 :   DROP INDEX public."IX_TrackingPoints_RideTrackingDataId";
       public                 postgres    false    233            �           1259    28661 (   IX_TrailCoordinate_TrailAllCoordinatesId    INDEX     {   CREATE INDEX "IX_TrailCoordinate_TrailAllCoordinatesId" ON public."TrailCoordinate" USING btree ("TrailAllCoordinatesId");
 >   DROP INDEX public."IX_TrailCoordinate_TrailAllCoordinatesId";
       public                 postgres    false    239            �           1259    28662    IX_TrailReview_TrailId    INDEX     W   CREATE INDEX "IX_TrailReview_TrailId" ON public."TrailReview" USING btree ("TrailId");
 ,   DROP INDEX public."IX_TrailReview_TrailId";
       public                 postgres    false    237            �           1259    28663    IX_TrailReview_UserId    INDEX     U   CREATE INDEX "IX_TrailReview_UserId" ON public."TrailReview" USING btree ("UserId");
 +   DROP INDEX public."IX_TrailReview_UserId";
       public                 postgres    false    237            �           1259    28664    IX_Trails_RideId    INDEX     K   CREATE INDEX "IX_Trails_RideId" ON public."Trails" USING btree ("RideId");
 &   DROP INDEX public."IX_Trails_RideId";
       public                 postgres    false    231            �           1259    28665    IX_UserRelations_FromUserId    INDEX     a   CREATE INDEX "IX_UserRelations_FromUserId" ON public."UserRelations" USING btree ("FromUserId");
 1   DROP INDEX public."IX_UserRelations_FromUserId";
       public                 postgres    false    225            �           1259    28666    IX_UserRelations_ToUserId    INDEX     ]   CREATE INDEX "IX_UserRelations_ToUserId" ON public."UserRelations" USING btree ("ToUserId");
 /   DROP INDEX public."IX_UserRelations_ToUserId";
       public                 postgres    false    225            �           2606    28504 +   EntityImages FK_EntityImages_Images_ImageId    FK CONSTRAINT     �   ALTER TABLE ONLY public."EntityImages"
    ADD CONSTRAINT "FK_EntityImages_Images_ImageId" FOREIGN KEY ("ImageId") REFERENCES public."Images"("Id");
 Y   ALTER TABLE ONLY public."EntityImages" DROP CONSTRAINT "FK_EntityImages_Images_ImageId";
       public               postgres    false    4786    226    218            �           2606    28667 5   EntityImages FK_EntityImages_RideDetails_RideDetailId    FK CONSTRAINT     �   ALTER TABLE ONLY public."EntityImages"
    ADD CONSTRAINT "FK_EntityImages_RideDetails_RideDetailId" FOREIGN KEY ("RideDetailId") REFERENCES public."RideDetails"("Id") ON DELETE CASCADE;
 c   ALTER TABLE ONLY public."EntityImages" DROP CONSTRAINT "FK_EntityImages_RideDetails_RideDetailId";
       public               postgres    false    227    4817    226            �           2606    28672 8   EntityImages FK_EntityImages_TrailDetails_TrailDetailsId    FK CONSTRAINT     �   ALTER TABLE ONLY public."EntityImages"
    ADD CONSTRAINT "FK_EntityImages_TrailDetails_TrailDetailsId" FOREIGN KEY ("TrailDetailsId") REFERENCES public."TrailDetails"("Id") ON DELETE CASCADE;
 f   ALTER TABLE ONLY public."EntityImages" DROP CONSTRAINT "FK_EntityImages_TrailDetails_TrailDetailsId";
       public               postgres    false    4837    226    235            �           2606    28509 )   EntityImages FK_EntityImages_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."EntityImages"
    ADD CONSTRAINT "FK_EntityImages_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id");
 W   ALTER TABLE ONLY public."EntityImages" DROP CONSTRAINT "FK_EntityImages_Users_UserId";
       public               postgres    false    220    4790    226            �           2606    28428    Horses FK_Horses_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Horses"
    ADD CONSTRAINT "FK_Horses_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 K   ALTER TABLE ONLY public."Horses" DROP CONSTRAINT "FK_Horses_Users_UserId";
       public               postgres    false    4790    220    221            �           2606    28440 "   Messages FK_Messages_Users_RUserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Messages"
    ADD CONSTRAINT "FK_Messages_Users_RUserId" FOREIGN KEY ("RUserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 P   ALTER TABLE ONLY public."Messages" DROP CONSTRAINT "FK_Messages_Users_RUserId";
       public               postgres    false    220    222    4790            �           2606    28445 "   Messages FK_Messages_Users_SUserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Messages"
    ADD CONSTRAINT "FK_Messages_Users_SUserId" FOREIGN KEY ("SUserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 P   ALTER TABLE ONLY public."Messages" DROP CONSTRAINT "FK_Messages_Users_SUserId";
       public               postgres    false    220    222    4790            �           2606    28677 #   RideDetails FK_RideDetails_Rides_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."RideDetails"
    ADD CONSTRAINT "FK_RideDetails_Rides_Id" FOREIGN KEY ("Id") REFERENCES public."Rides"("Id") ON DELETE CASCADE;
 Q   ALTER TABLE ONLY public."RideDetails" DROP CONSTRAINT "FK_RideDetails_Rides_Id";
       public               postgres    false    229    4825    227            �           2606    28682 #   RideReviews FK_RideReviews_Rides_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."RideReviews"
    ADD CONSTRAINT "FK_RideReviews_Rides_Id" FOREIGN KEY ("Id") REFERENCES public."Rides"("Id") ON DELETE CASCADE;
 Q   ALTER TABLE ONLY public."RideReviews" DROP CONSTRAINT "FK_RideReviews_Rides_Id";
       public               postgres    false    228    4825    229                        2606    28528 '   RideReviews FK_RideReviews_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."RideReviews"
    ADD CONSTRAINT "FK_RideReviews_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 U   ALTER TABLE ONLY public."RideReviews" DROP CONSTRAINT "FK_RideReviews_Users_UserId";
       public               postgres    false    4790    220    228                       2606    28555 /   RideTrackingDatas FK_RideTrackingDatas_Rides_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."RideTrackingDatas"
    ADD CONSTRAINT "FK_RideTrackingDatas_Rides_Id" FOREIGN KEY ("Id") REFERENCES public."Rides"("Id") ON DELETE CASCADE;
 ]   ALTER TABLE ONLY public."RideTrackingDatas" DROP CONSTRAINT "FK_RideTrackingDatas_Rides_Id";
       public               postgres    false    230    4825    229                       2606    28538    Rides FK_Rides_Horses_HorseId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Rides"
    ADD CONSTRAINT "FK_Rides_Horses_HorseId" FOREIGN KEY ("HorseId") REFERENCES public."Horses"("Id");
 K   ALTER TABLE ONLY public."Rides" DROP CONSTRAINT "FK_Rides_Horses_HorseId";
       public               postgres    false    4793    221    229                       2606    28687    Rides FK_Rides_Trails_TrailId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Rides"
    ADD CONSTRAINT "FK_Rides_Trails_TrailId" FOREIGN KEY ("TrailId") REFERENCES public."Trails"("Id");
 K   ALTER TABLE ONLY public."Rides" DROP CONSTRAINT "FK_Rides_Trails_TrailId";
       public               postgres    false    231    229    4830                       2606    28543    Rides FK_Rides_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Rides"
    ADD CONSTRAINT "FK_Rides_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id");
 I   ALTER TABLE ONLY public."Rides" DROP CONSTRAINT "FK_Rides_Users_UserId";
       public               postgres    false    4790    229    220            �           2606    28457 1   StableMessages FK_StableMessages_Stables_StableId    FK CONSTRAINT     �   ALTER TABLE ONLY public."StableMessages"
    ADD CONSTRAINT "FK_StableMessages_Stables_StableId" FOREIGN KEY ("StableId") REFERENCES public."Stables"("Id") ON DELETE CASCADE;
 _   ALTER TABLE ONLY public."StableMessages" DROP CONSTRAINT "FK_StableMessages_Stables_StableId";
       public               postgres    false    223    219    4788            �           2606    28462 -   StableMessages FK_StableMessages_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."StableMessages"
    ADD CONSTRAINT "FK_StableMessages_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 [   ALTER TABLE ONLY public."StableMessages" DROP CONSTRAINT "FK_StableMessages_Users_UserId";
       public               postgres    false    220    223    4790            �           2606    28472 +   StableUsers FK_StableUsers_Stables_StableId    FK CONSTRAINT     �   ALTER TABLE ONLY public."StableUsers"
    ADD CONSTRAINT "FK_StableUsers_Stables_StableId" FOREIGN KEY ("StableId") REFERENCES public."Stables"("Id") ON DELETE CASCADE;
 Y   ALTER TABLE ONLY public."StableUsers" DROP CONSTRAINT "FK_StableUsers_Stables_StableId";
       public               postgres    false    224    4788    219            �           2606    28477 '   StableUsers FK_StableUsers_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."StableUsers"
    ADD CONSTRAINT "FK_StableUsers_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 U   ALTER TABLE ONLY public."StableUsers" DROP CONSTRAINT "FK_StableUsers_Users_UserId";
       public               postgres    false    220    224    4790                       2606    28578 E   TrackingPoints FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrackingPoints"
    ADD CONSTRAINT "FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId" FOREIGN KEY ("RideTrackingDataId") REFERENCES public."RideTrackingDatas"("Id") ON DELETE CASCADE;
 s   ALTER TABLE ONLY public."TrackingPoints" DROP CONSTRAINT "FK_TrackingPoints_RideTrackingDatas_RideTrackingDataId";
       public               postgres    false    233    4827    230                       2606    28588 4   TrailAllCoordinates FK_TrailAllCoordinates_Trails_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailAllCoordinates"
    ADD CONSTRAINT "FK_TrailAllCoordinates_Trails_Id" FOREIGN KEY ("Id") REFERENCES public."Trails"("Id") ON DELETE CASCADE;
 b   ALTER TABLE ONLY public."TrailAllCoordinates" DROP CONSTRAINT "FK_TrailAllCoordinates_Trails_Id";
       public               postgres    false    231    234    4830                       2606    28640 L   TrailCoordinate FK_TrailCoordinate_TrailAllCoordinates_TrailAllCoordinatesId    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailCoordinate"
    ADD CONSTRAINT "FK_TrailCoordinate_TrailAllCoordinates_TrailAllCoordinatesId" FOREIGN KEY ("TrailAllCoordinatesId") REFERENCES public."TrailAllCoordinates"("Id") ON DELETE CASCADE;
 z   ALTER TABLE ONLY public."TrailCoordinate" DROP CONSTRAINT "FK_TrailCoordinate_TrailAllCoordinates_TrailAllCoordinatesId";
       public               postgres    false    234    239    4835                       2606    28600 &   TrailDetails FK_TrailDetails_Trails_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailDetails"
    ADD CONSTRAINT "FK_TrailDetails_Trails_Id" FOREIGN KEY ("Id") REFERENCES public."Trails"("Id") ON DELETE CASCADE;
 T   ALTER TABLE ONLY public."TrailDetails" DROP CONSTRAINT "FK_TrailDetails_Trails_Id";
       public               postgres    false    4830    235    231            	           2606    28612 &   TrailFilters FK_TrailFilters_Trails_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailFilters"
    ADD CONSTRAINT "FK_TrailFilters_Trails_Id" FOREIGN KEY ("Id") REFERENCES public."Trails"("Id") ON DELETE CASCADE;
 T   ALTER TABLE ONLY public."TrailFilters" DROP CONSTRAINT "FK_TrailFilters_Trails_Id";
       public               postgres    false    4830    231    236            
           2606    28624 )   TrailReview FK_TrailReview_Trails_TrailId    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailReview"
    ADD CONSTRAINT "FK_TrailReview_Trails_TrailId" FOREIGN KEY ("TrailId") REFERENCES public."Trails"("Id") ON DELETE CASCADE;
 W   ALTER TABLE ONLY public."TrailReview" DROP CONSTRAINT "FK_TrailReview_Trails_TrailId";
       public               postgres    false    231    4830    237                       2606    28629 '   TrailReview FK_TrailReview_Users_UserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."TrailReview"
    ADD CONSTRAINT "FK_TrailReview_Users_UserId" FOREIGN KEY ("UserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 U   ALTER TABLE ONLY public."TrailReview" DROP CONSTRAINT "FK_TrailReview_Users_UserId";
       public               postgres    false    237    4790    220                       2606    28567    Trails FK_Trails_Rides_RideId    FK CONSTRAINT     �   ALTER TABLE ONLY public."Trails"
    ADD CONSTRAINT "FK_Trails_Rides_RideId" FOREIGN KEY ("RideId") REFERENCES public."Rides"("Id") ON DELETE CASCADE;
 K   ALTER TABLE ONLY public."Trails" DROP CONSTRAINT "FK_Trails_Rides_RideId";
       public               postgres    false    231    4825    229            �           2606    28489 /   UserRelations FK_UserRelations_Users_FromUserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."UserRelations"
    ADD CONSTRAINT "FK_UserRelations_Users_FromUserId" FOREIGN KEY ("FromUserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 ]   ALTER TABLE ONLY public."UserRelations" DROP CONSTRAINT "FK_UserRelations_Users_FromUserId";
       public               postgres    false    4790    220    225            �           2606    28494 -   UserRelations FK_UserRelations_Users_ToUserId    FK CONSTRAINT     �   ALTER TABLE ONLY public."UserRelations"
    ADD CONSTRAINT "FK_UserRelations_Users_ToUserId" FOREIGN KEY ("ToUserId") REFERENCES public."Users"("Id") ON DELETE CASCADE;
 [   ALTER TABLE ONLY public."UserRelations" DROP CONSTRAINT "FK_UserRelations_Users_ToUserId";
       public               postgres    false    225    4790    220            �      x������ � �      �     x���Kn�0���S�1,J~-c$V ۭQ�]��6kѢIb(!PЛ�g�^�}�� RDP �8�N��l<��nF�������$���PJ_C����������߰9ܛ��]�(s��{�S�*w(����d��>�g�����|���,���),t#�Z��*����`�L�:�³簵Ƅ'#x|VZ˚N`�ex�>	ev���	_��sJ���%��OO��9-Ob_��g�Tu���� ��S���`����^z��RS!P�3*����A`+�Ƒ�gw�����a#��	>���������'�n<�8dND�T�|}�mc�S����q�l�l��;��Ղ-��*�:���R��Na%�=���B[��+̠0����Be�9,)���/�/#T�tE��p���	,����!�i�ک�C���,�"�R��ڲ<B�ٿ��@�q�OVH�]��B�b%Y!�;l"�r�Y1d+q>K/0�[J5�5hb�g��d�Ah�=�8�9<bsvt��n➆���7n�'�      �      x������ � �      �   �  x����n�F���SL���p�f�E��H��@�t��璦E�I	����^��e'1��7G�(��|��3IQ�47^*��R�*���Z�<u�\]Ԫ�JU�M^X���ӟT>��E���ꃻ�fA��N��,�a'���w�M3�Gџ�G��h��N��������F|�F�ص�1�����F�u]��n]C�m/�aw�����Eh�q�Nx��y���J%��DI�D��N�י����~L�WIelС����Ԧ0�U����T��S��krW�
�[{͕�C7o�[�tFql2p��u�?wb�1���wx��=w����u���a�i�dZ~�da8I���@�L\NR'*���+o|Uy���&�M�v_b+�Ŧ9��-sYd
�.H_(/-�N��ʔ�/F��z���n�$&�s�;��f��=A�i�>����>�}+ꎶ['�J��郞�%{�e����/���v��~��:<��z�u}��~��Gw�Q��t��C�'�����3."�ys��6�Ut�0!��r}��I�ZV��*�*��X׼U�>�,`�����0�L'"e`�������#�e�9�C3W�ǽk�i�9=`z�];m������<2۞�!*±�z��̀J�7�ڐ��R�$�N�2��-L"��ܓw�L�X�����C�D�8�t�z���ͱ�gP��0+u;�'����*y63�s1e��*G�
�SD`U]H� [:sY�������F�����_�i���#��`�$��%�L})}2i�I�>dCe_��j��Rv��d�T��ü�jY<�mz�\T��M�6����$�5���,�2I�VY�*�U��_�c
|��`}?��{�[Ri���Q��"����	�ɰ�֊�J�gP&�f�ge� �I�y��F|zb��9jo7��3��̟���ݹ�u����x����U�l�������V2s��\�D�Ji,<[e��l�ۃ�!+�5.�6A���߶�5���o�����	�������ƝY��Ns����:����
�x�8������E��/-2����G'>���:��Z[�m_�	z�G���	�ˊ��
z�|	��i&uEVZo�T�O��
M�K
u��wэFְ���h�F�Gg �&*�؁��V�w�=�|`���塌����F��@i�U�4��,/s[z��Z��J�s��sc�"e���k�K�i���];�ty��F)K`2�^Y��vp:+�,K�J���_�{��w4E����.�*j�d`�ln*��j�]H��|������a7�v19ލ$�!��f���!�l�p�g��J�cw���rh�:4w��G�,g�hS;��(��M��#�";�/�%��k�{z�M���b�HܞQYo�$�h��eCbS�t�:�������.� ���e�]U�Nȭ�z��|��'�����r�I�<{�hڿ��1��y���L��֔$6Jd��$�Һ���u�ꄸ�Z"�x��Ȃ�i�t��7;�'��ȹ��4�u��/�b��ڼz��_FIVV      �   �   x���=�!Fњ�K�?�������$)RE�1MAq��`:��h����5�'�&��ԧk��}|����H)�ԷĘ{�2�u��&D2H$c��uoku��k�;H� w�{���
�8���0hͧ��Yp.f�q�ER�����+!�L"x�j��*�X�N#23���j�a(�N��ս��+0�w���(xW��x�y�D��R��T.(�J��R�k��~]�)b�      �      x������ � �      �      x������ � �      �   V  x���Mj�0�ᱮ�󢜟�$w]���:jK;� ��|���}�j^���&��R��o��E�^�X�,z7]���Iv���ZL�n?�Ǉ�C���^Ɛ~|���W����\Y��T(�Ee��_e
���9���:[fBʎ�2�
��t��j��2'ec��Y�d*�e5�:(K&���H�*��U{[�O��8K&��u�, m2e��e�M�,Y B�d`55Y Bf�,Y B�d`�-Y B�d`5mzb�]�d���8�f��Fʒ	 d!�,� B��Ȓ��b�iX��LIY���HY��,4 ��'@�
K������}]�oB��      �   �  x��Y�nG]�_Q��P�G6�x #�ld5�z��M��"�r�!{�؜jR2ɰ���L,@�M�>�9��j�<s�)�T�T9ih���}2���k��B*m���^�q�r����ɜ�$�>��>�=y�B>��@v5t]���ɿ��H�]�ɧ2�u���m�aݑ��oC!ے�#>?�q�n���;���LP�c?p�����h��?�JL�%O��H2qI}���Y4�3S�$�$*+�߄!�M��lᜬ��aU�{�sK�9}́��|ˁsUr�je[.Q/3�̦(Y�Np�$�$u�+b���ZG�����"���
1E����Y��Tq�iP6�X�漺`DY�Y���XX�R�)<�Kk]i�!S�S�!!F)-g�����Jx�>��c�R�l��.G�r.�X��MWX���>���qm�)S��I���w\�O�>d�&����@��Ю�UW�c����DD��ʲ�T��)1SiK����\��s�����|¡����U텼����B8�-M��)!�
Os��3���"�.�=����߃�=y�`��!ZE!~!�E^�C��e�X�:�Z�d��Ʊ�_�aWu��1&!5�P�X�=�)OT�ڢ��}��i��~, ����O(��m(Ax�u��rt��hA.k�+-iÜ��Щ�1�x�?��%�
���)!������$�1gE���
YΊ�_�ކ��SA�v�C���O���o㔬WH�u�V�����g�v�-[��]�8m���G֛��=��
:o��m��DUTPRY�Af+@�����T���f�ݖcΧ	;M�M��H_�W�dN`|O�x���G8G���[=�~U���-����|n�N�E����f�S�A��Y�Xk��m|Վ	#��7�%*3��x,��u�C��n��N���@:�G���奺c�e�Г�"C��q��Ӡ��U$-2F�+Wy=�ՔdQ�Y�E�8	Y��%PY��E���
�-��C���(P�vӛЕ��ke�� C�T����@�le�F�������ǯ�����F�U�5�৐;B�[	��d:~��5���c�7�!O]����o�.�uY�� �E�Հ�B�Xy�"ZQbt�+/�nI������;Q�Ĉ�yW�Ƽ���2>�mqM�eј�A��I��*�����1�D��Joag`}��s�I.�`��i�g���,"��p�k2�Vxݚ��I>F f�YX�d N�rh�tO.��qg3���V+W�����Hأ�mM�ΛP3�r�!�9jy
���r�q�x�}�9��܅E�wIY�W������<.�O� ���;r�9����Ͽ�ㄅ�c�2M�V�mi/�:�ߓF���k�5�Q6{,$��=�tb|�CO@�
xܶ"������x2c'e�Z.q�\�2+���I��X���,��ho\����u3P=��|�w�p ��p*��r�Ĳ��Wi4/I�E���k�Ç�+�?���c�E�П�=�׆7�w��a���� Z��{ z�ֲۗ�fv�<������%xe���R�O,$���E+�w�6u�|su�Ÿ��D��Ж� b<<�����<\�v�u�+/����c��dp��Pm��i��"�.V�W�;���P�2�kMx^���Žizk{�@���Ȥ���T�&nm#�>�R��&�O�?n6�X�A�]����`Pa�3WӢ3�����<Ц��hz=r�� ^������������%����t��S�%#Md߬�W�e�a7�q���K�9��"L���VQ�R��հ�hw!ڦ���^��>6sq:{"/�:�V�t�G�y���4 ��&�B��v#p;�p#�X��k�1m'Yg��tT� ��(�w&���\��"w��W};�nYǓ�""��*.`AM���>.񧋎ynO=N��2>��P
���V� 5Ys�B���=`��[t����8�/��M�_�0&`��]vх��5u�P�� �J��1��y��E88��е#�����ڽ�	�t��0
�Zs�g(OII/��iɁ���7�Kcnc�W7�����E�o�[��T$��1'ԷH��3	�EY�"s��>·@���6#=����[�nI�u�������ڞ�9_��bhr!0Ŭ
��tDïM����L�!5Ws{���i]�H?[o�d��Q�M��j�K�-�V��րU>g�w&@���-s�/z�H]���۸!���'[��"b���v�'P�$�M1��礖�닞�����!�4N�}��ݻ� ����      �      x������ � �      �   �   x��ѻ
�0�9}��RɵՎ"(�VG���%���F���3����`�����q!�|��x>O��W����q5].�2��	�=)���e�ɔ��ILHA�˱��I�G�&�_bh�X{e"X*���
_�/G�d8Zn׋>�%�Ze�v�;�C�@E��6F��T�ƃ��}�l��Ȩx�7^]v��c�����Py��~_}t�?���,�Y�q�$w�(��      �   ^  x���Ko�0�ϛ��
'�1HE�P�b�&�jldox���%���z���Y�8�1�T��W���4�FX��a�V(�n�Dw4��F���5��)fxB��܀cK�F�6�'�#Y˫���9O�i7QH�2�W�v�y/�d4���ƨ@�0���t�#?cn�*y�����v�PWJ�R�-�*��a��Va�N�;G���Ԛ������
��a�T������ߨ���X��
���"D�2�>�h���,l� ��YQ5�Q|N���	A�N�q��r�,ɲ��V�Z��r�\�I���xeN����.��J-�<tk���]̸.m�x���-���CA�      �      x������ � �      �      x������ � �      �      x������ � �      �   �  x��Tˊ�J][_�?P�ߏm Ʉ<�0�U6��Պ��H�?(�0{��-{r7�@/D�N�:����j�`)y@r�i>o�C������2�4�؎�q�W��e�m#�0/�~Oˮ�)yߒT���F�*P]��|�t��$����♪��*2��u�~�)�t-0�V�6� �s`�sP)�۰�$����U�c?^8��}V1���[Ӑi�!)�
h�m�p]���[���{���a�.�V�P�#�:h�Ny�^6�D�XlI��{3�J����/b�I��E�O4^�zmelyc�$$����/�(o�@���)�Ů����X5��끉��-��eȄ!�D1�x�ۉ���.^	[�V
�gu�쌃f��������x8���2��~ۂmx��B�H�Kt�{VS��IN�rl��w{�gF����"�/�׶ᮋSJA�k7��l���5FY�2ܡ�&�X�l��i��u>���<�/O���0u������
D� ���1q���>�󶉯���$k)B,Lٔ�1F��{�����W$��#�_x�$a��i����,�B7��T���;��U|��AJK��P��8"���x�#�P����D����T�V�ڡGN�˰�u����k�H[4�X8�W����ZP�e-�!��/լ(*m���؝?�ʵ���u�4���ﺮ�V�r      �      x������ � �      �      x������ � �      �     x�����G��ݧHP�p8�K7��6gvƹH�3$�?P����X(9�s��*T��/�dK!��+������R�b6���������|z���a�9g�0�1O1�!B̿��˛�w�h�k�	c��E ���^~��T��&0�
�F�5�Jع�>��r�}?����MYw�Xn���)$D�mH3�ƥ��2�4A-g ��Rj���~>�����p5�\��U-
�F-t�%rE)���(cD��#Ԣ�y�b�����r�d���rz�g��o�8�+R��5�i#��,!w�X"PF�0 �Ήlh,������~��f��hҸ��7��3GJ�:�`sKR��Хn�!�G���/v��~�q�!�����o�$ͼ�*n$	P����
�[�Q�� �o�����w5$��%�f$o�(s)���
�5��jN�@�K���������B�Iu�oj��R�<��I6�̞��(��!�VHxp����q�׺�����j0�7j�aUX��@IȃRB�K(i�ן���x۰k.(�t��vM)m�"�^+]zTϣtoJn fZ�E��K��ޟ~�A�J������E��Z�&`=,*�z}����"�,x��gre/��ukI�l�D/~�n-������]۸fԯI�?����%�Ð���3�	���D�6���	��o�9��y�57-�]�;;.~��u�D��G�+6.'�G�T�G!�-���i!@�4�Mo�t�|z8;��)��DZ;�P7j�i�s7��#��:      �   g   x���9�0k���R���4�$�)��.��[0�u-M͌ҬNi�F�hw�&\�M��F|���_��`���o� F&!��7���E״*�,���"	      �      x��\Ir�H�]3O�E�Y�u�<Ԧ��LJ�%k��  A��D���Co��m�7�{ݤO�RR�T�iV�e�  ϟ?��DIVT�������M�zv�F/03EI�C������m�o8�Ҍ�Ɵ�'�������tl��ɶ-�!?��bc	QG�Mwx`f�����ý��|04Si�,N�rw���)��i�X3
��C�EM^�8I�uIRxH�Ȓ.+���'p��䄦 3�^�����.���_٪��1v�J2nG!Npha!f2�g^e�%ʘ8��Ž���]�r�,A�d*Hi8�s����&��,ž�0�Bw�
��%'����1��f���9D�qf�^�8(t���͏p-��<N��>���!݄�aJOH�������c�������������-�t�a��8a<Č���	\x>ˍ�(@��z��qb��gpy8+����V���e�����Eٜ_�%��
��%��\n�e|�`�	
F���<��yy���I�?�QH��]x~xc�9Qb��Ƃ1��Ony�a������簖�FL�����G����H�H73�������Yg�p���ܵ��
�=X��8yu��m�����ha7����%��d���dY6'h�e_`�C��*L������ƶ류A 4������N,�E�� �%	�|�C�8�(�R�È�.\���'�: �����̰�eyB ��� j��"p$�	��C
@Z����P�(�Fg)��� �����8������y������^��q�S�1}�_(����u
}�J����w�=i����Ȉ ����D�hLZP��!�2]0�����Ei�%�@�eQ�Q�K_���=�p
�v�ߧ�"��A��h�!3������p�^h!��v� c�{J�"�؅�,yɹ;��ȶ�]���Sk�{w>��_��^���S����<Ph��,�¶"+X���**2_�\�*ܐ� C�$�V�>eI�t ^���
y`�oi�=9���Ep*h�U�5��{$�S`m8{�2eߥ�+�a	S�����q �n���Eȟ��À6	�e�y�s埉C9@a�1��o�ʳ+�2�{����R´?��W���kXf���;�Zܛƚ��U,§e��)�ُ
� q%cr�6~���Cx ޏ�A�= U�h8�7_�y>tS> ���#�8��l�*�  ��Za+�m"%�P�ʦ����ޜ�[�&�G}���O����,�Gcq~,�X๝=P���ISa�V��+芠H�,�RS�mN��6T�ve�P�]�*~���R�i�
�3WSp�0F�)��ҢD��V�\����=:�W7(0���ɀU�SJ�%���4�3��� ��p�9\x������$t�O�8LދB�pnT�?��
'!�e ��$�'��sA3����-�ֻv)}�/$�����Q���ogL)���2�g�O�>������3�n��#�c���u3�2��ĻI�s�	^e�$�з�@�%Q��>�|������䥓ܤ��Ɛ�3�bx�����a���6�����,�Ov3[	r��i�"���]�筣9�~5�y��:bK����e�B4cI�T��4��dK3^G�a4l��g�UDMV� ��X6bN�H.D̀3\��>��O�����Ch��W
U8p�r|�d$� �>U���70[�}�]��v��v��tsÿҹMG��A�GT� V�j��d�����Zr0&$�`@L5���{� �T>Q��J�A��3� ��L`������{%>��L��k��j�����:ݫ�a�`�%&�Kw&�=��ª�-��������$xq'��t-Lyx�j�+�p�IWN�ԑ�3�A-R�tI�t^��)��[�(٠ʫ!���*HU���w &F�h�C0\��DD�5��	zg����0鍛)�h��8NQh�P ѳ�s<%��� I��X�P :�D�&��Ko��?� ���K)#b2�>�g�Yo>Ii��%DhE7x��D�����f4�%��H\z���T�Y����톚��uʯ��L�$�w��ȣR����ER�ZB`r��/TV;$o��$ �B�����"3E$���]�t�S0��+}�m�_�v&ϵ(C��3C,�@�_�q�����@�\����[y-0+'j���<��T��8E�Ar|f��j0��~ �Me��ZQ�4e�c�b�PNc�-{�6����;f�(I 9.�%�\��h�g�քvh���IV�Q�9�N@��U�D�V�A�mAd(Pb�$L�!��jd`�C4>`�<WJ�
��� q#�+Rv����l�W���G����������'Ti��T�'����JA�]O�z-�QH=�bZ�k���mH��-+x^�W�����;U~l����}������N�q{�~<p!oO����H��N=h��_Y�	4u�4��Dd�Z4E��DY�>O�~�JK�q��B	\�����D�¡��2$|I����M�w@�͵^��=ۦ5B
�t-5�I� ���z�$B��K9�D����� *!��r.uӱY�$R2�C d�O���-�e,3�����P"��A�1
���r@��JojL�����������@�U�-�����t�qA����E,�_��x��rr/��|�j���Y�ʷ��ݿ0W|����N�2��]H٧dtp^���������bGDVR��y�1C e���/az&�����]R��][�F��_���i��}���{+�?=Y�(�����s�"��ɢ�sMK�����M�+_�ES�*_yo�@㟩�R��0�F��?J��WF�z�*E⩗`�����K�_�eV:ׂ)�ď��M���$H��PH� �{����R>%��k������Oζm|�
�
7�@_�lnYP&�����4��+�m9���Ohi=�y�k�r�J�55��ư�t����RqL
6%lYf�a0܁X��2L�[aZ�SIj��I��M�A���}�����$F;>��e���/������6s�r�sյ)�㴍��NG.&�dC��at���yŨ*芦���Z�\A2DY���Ă�V*��H[�c^�rca��H�r6�\�ox�Zt{O�ǥ�y�0�g����.O;d#�xl�Yך����VӐ�_I�n*_C�Ȟ��A[�������}�6�;�y3�}�w3������^���~��g��f�g��z�>E�ųfz��Pw��0���A}�(�M�UEᚚ)�(��.Vl5VI���J�+��,�4xS��qY�vI�?��� �궲�Y0)z���ȢW^� 2�
���/ʚ0p�'�mҧ̙	)9��Y9�Q�l)i	�<����Ɣ��r&%� �h�
b̻��x�2�ly̔p!kG�Z�Ο��܊e!*ʩ.��i��0M��s�����E0J���|Pj��|�}���g�5jZ� s�`AP[���@�+b��y��6OGBÇ�r���P�t�#����˸x y��[NB�8�B�na����c�ޝvZ��d�X>��p��`^d��/��wX�Ǐ�Y�@����V�%]�dY�9��i���V�@<��WtӺuS2* �E�$D"�`"��xI*����2-8�TX��Wv ZP� >���h��liH��0)0��p���G���6ċ`��"�½ �e��?��ȅ~I�L�G��-�!A��%!Ee"�2�6�M��@�� ���_N���/�;�}�C��&����w����}u��7���WW��&��UN0-D{(�"%�y�P�2!��%��$�����$Z��C�m:��.!{eE����"��q��	���ǉ�0.6&D�3k�YR�*�������wj�0����f�Z�9�� /E�~�zI���e�����ԃ8�ATEQ&A^TME���Ʊ�t��U��x�щȤ#�I�(�3<�F��H�����v;��ޗ���Vn~�����6�.Ng�Ŵ�/O��� @  L�p����ce�����V��E�F���� �APj!<`i���s�L����Ǔ�����T-�>gn�Y��n���\=X/�(����҇c]�e�� ���4���))p��)�|j
P�=n7�T׼dH�ř4��`5����1f=ng�<;ݝ�ƙ�ی7�P�OT]�4��51�TSԑ� `UAtM����[�A��@�>I[��-�˫`32�#�O�)����_xOG5Q����'m�Ǳ=N��~�	jY@�eN�e��,��i��a]�D�B_[�ڕ)�̅x���\�1��ȆX5 :�*|��<��ķ6¥���M�u!W��hKi������S޾{���q�֛�U�y�Πiq�mh��j�w�,���J�?��3��f���l��"��:���sK~���¿t'�<��r��[�e�*Q����^�ł��S���/��W�02!QS��(k�Ru]��9����>O~�Z�*��ć�Dy�X������r�╲���S}=�c�����H:F2�ç�����x.�f��_�k�	�Ub�"pMHԀ�j"���$��Ufxk�g�� �*\�|R�Ѣ��=��9��XtD'9���s`Ʃ��;l���������&��Z|F�o��K�jE�%��k��s"�7-EC2MU�BP��W	���z� ���,��Qy��VD׋}|<>�Om�����hp'Ky���n\V��b;Ƀw|Z���/���*������%����"�Ė��0��CFR����!6V1����ْ�,�'� �S�5�pܦh�q�����br_�V����uĞ2at�ܾ�U8'��z�H��Hu��-��uS�TY�����r������B��$2�	W��i�9��
��b���|%_J֛h��ȹz�dzwؾ�졻��ű{�s��a-�T!�xI�UYj�@��)�X����TE�6�{k=cȍ�� �Y����*z`���֘���fҾsٔ5qسF��V��^dꭇ��֖�|w?lg��Sg�5&�Wd��(z�6%dZ�js�Xi�� �r�rm���Di�rR�]b�BIc��6�}ӿ

�G�Nt��C��'��~��x���^�mse�O��E�݂�RgzVn-l(2hFUWQ°l^3t�4�;�V$&�YAm�B���<J�� �2���ikH0�{ڱ��<?���VƊ5E��#�<N������F��/Z�"*��9V�N ��D]��+���WĐk�x���&ȉhş�Di��r�����x2�Hà��
S������@^��(:q�b���ʍ�|�/WK)z��Z�tI�9 ���M릠�.����4F��ۅ���,�7�>&30m�8��c��
�Պ�4�=���p�<	�d5J����]�1�6r7���<��
��H���]�dE�%N�7eU�P�d��`*�2�e+9��ַ��#�mJ��O��Y6|n��`����SЙN7y��M>)�Sw�����*���<K�rN�b��j&&�$�Yk�3HZ�$$��,����W��|l�g�1A~a`7���r4��8�#+d`?�\�s�$�d���_d֢���y�>j��}!-������xa����>�~\/���$i:�Dk2ր@*׃�{k �������U��*C>�� �I0���
��p7�H�X����>�G���#���o߷����݋;�{�2|���Q��q��(T���B�8�Ęb/E��(O�s��T�%���Ŵ���B�Ǯ��~.v�����$f5��,�n��b�yڛ�z
�-!��"U
����l!5��.6]���6����EO���?�~%���v��k��X��7}j#�K��]�G�vd�'�"L�,����*K��ֳ�ܘ�,�e���}�nE,���ӝ�)�G�I��û��,���f��=���~���1�5s)�����}��� 
�Z�0>��3�ͳ�!f�[�	��M=�����ͯ �p0w�@�_�E�y�[��e��|��L��V��'8�
6AlXY�3^x��d�L��[��Cm_���0����W��^������Z>��P�{�D3G�x�����&#1ѕs�d�'�Ykv��e����i�P1c򱵞54�,up��.���O����/�qmN�"���"�O��
��Φ�1:���Vx���f����������ӗ[k��D�KQ�����x]��DYq'w���a����,��/��l�=��4�S��e�{���s����0�&�������p���٤B`���ɑ
-~�yճ���K����]���P`W��r��3n������t*^����^*��&B̬2m�D$��O�UN�-?���QW|>�(6/O�9{.k�q<�OW���3y��c�l�oD?��Z{�Vf���$��dN�@B�Z_ZIB�O��i��Y	�d�"�v���L/��=����O��܊�a��J��s[�{�.���y�dnQSK���	�$H�*T���zfi�>��}���%�|JT��E�~�t)���n���wX�AD���L@c�ǽȼsy���b���rU�3��f�XQ��PQ���Z�����,�-<��jl�O���*�ֹ��nGZ{��Xu�޶�.8s�}Nr���f�ޜ�y7���qMe�(���*���2K�$�:G�XUE�Bn~l�g����3�f%nʐ���,����r8D}�o��)Z��wGݍU)�L��xP2I��u{�<��v��4cs5�9�*h2��ĺ�$��TI��0�΁Ĭ�[�Ek��o��%�]�^;[��9D@o�Fpp��{�2;ڳw�[�v�����B���`N���^�Z���!�T�&jrSP�d�<q����B ખc|l�g��Il���<�{d�$J��R��:�S�;��Q_8;�a'��������͞��x�^9���2��e�p�*�@����$`pd}e���{m���p���m�3�s;C����h��	j�,�q�9�[�����u���#�՛���� ���z���D����~����Wl�`.k9��K��d5���-$��9U���*
��psk�!�2)��#c�3]@����m�k̬����@ta%�ި ?���sr���aI���G��<'�Cz.Y{�Y��4�����+y�.�Q�t4�|zHlD���蚴Yۙ��'�K7�,�>!�4�����Qv.pBW��-����Z>�>K�|��B��~d��Ҟs��2�ݢC���&����>]x[��6��Ύ|�Q~ճ��K/7�'����ɲU�d� �{���w��Y�0V1�W"�6�������Y���7��(AY�~�����d	�z��0=�����O?����P      �   �   x�mα� @ѹ����`��.�ulb�K�@���K[�~rs��d5(@��-��of��=��U ?�ռf��Ӯ�ة���0�N?�
��&�e
�[ӡ����#(q����j�Ĵ��1�Ò휩�y�]*.���A"�K2[kx�0�LK�^���"����U�     