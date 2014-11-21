using Microsoft.AspNet.SignalR;
using Newtonsoft.Json;
using Owin;

namespace RaceClock
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            app.MapSignalR();

            GlobalHost.DependencyResolver.Register(typeof(JsonSerializer), CreateJsonSerializer);
        }

        private static JsonSerializer CreateJsonSerializer()
        {
            var settings = new JsonSerializerSettings
            {
                Converters =
                {
                    new EpochDateTimeConverter(),
                },
                NullValueHandling = NullValueHandling.Ignore,
                DefaultValueHandling = DefaultValueHandling.Ignore,
                MissingMemberHandling = MissingMemberHandling.Ignore
            };

            return JsonSerializer.Create(settings);
        }
    }
}
