using System.Collections.Generic;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using RaceClock.Models;

namespace RaceClock.Controllers
{
    [HubName("timerHub")]
    public class TimerHub : Hub
    {
        public IEnumerable<RaceTimer> Activate()
        {
            return TimerController.Timers;
        }
    }
}
