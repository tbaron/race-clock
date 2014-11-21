using System.Collections.Generic;
using Microsoft.AspNet.SignalR;
using RaceClock.Models;

namespace RaceClock.Controllers
{
    public class TimerHub : Hub
    {
        public IEnumerable<RaceTimer> Activate()
        {
            return TimerController.Timers;
        }
    }
}
