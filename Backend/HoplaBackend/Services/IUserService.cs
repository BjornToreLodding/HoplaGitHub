using HoplaBackend.Models;

namespace HoplaBackend.Services
{
    public interface IUserService
    {
        Task<User?> Authenticate(string email, string password);
    }
}
