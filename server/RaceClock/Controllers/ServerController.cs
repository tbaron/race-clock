using System;
using System.Web.Http;
using RaceClock.Models;

namespace RaceClock.Controllers
{
    public class ServerController : ApiController
    {
        public object Get()
        {
            return new RaceServer
            {
                Time = DateTime.UtcNow
            };
        }
    }
}
