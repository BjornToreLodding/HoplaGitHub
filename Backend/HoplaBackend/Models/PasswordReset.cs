public class PasswordReset
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Email { get; set; }
    public string Token { get; set; }
    public DateTime ExpiryDate { get; set; }
    public bool IsUsed { get; set; } = false;
}
