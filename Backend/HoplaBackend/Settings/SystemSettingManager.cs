public class SystemSettingManager
{
    private readonly SystemSettingService _settingService;

    public SystemSettingManager(SystemSettingService settingService)
    {
        _settingService = settingService;
    }

    public string Theme => _settingService.GetValue<string>("Theme");
    public string Language => _settingService.GetValue<string>("Language");
    public int PageSize => _settingService.GetValue<int>("DefaultPageSize");
    public bool EnableLogging => _settingService.GetValue<bool>("EnableLogging");
}
