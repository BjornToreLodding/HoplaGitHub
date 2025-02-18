# Bruk en offisiell .NET runtime som base
FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 80

# Build stage
FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["HoplaBackend.csproj", "./"]
RUN dotnet restore "./HoplaBackend.csproj"
COPY . .
WORKDIR "/src/."
RUN dotnet build "HoplaBackend.csproj" -c Release -o /app/build

# Publish stage
FROM build AS publish
RUN dotnet publish "HoplaBackend.csproj" -c Release -o /app/publish

# Final stage
FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "HoplaBackend.dll"]

