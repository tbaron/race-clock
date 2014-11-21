using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Microsoft.AspNet.SignalR;
using RaceClock.Models;

namespace RaceClock.Controllers
{
    public class TimerController : ApiController
    {
        static TimerController()
        {
            Timers = new List<RaceTimer>();
        }

        public static IList<RaceTimer> Timers
        {
            get;
            set;
        }

        public IEnumerable<RaceTimer> Get()
        {
            return Timers;
        }

        public HttpResponseMessage Post(Guid id, [FromBody]RaceTimer timer)
        {
            var matchedTimers = Timers.Where(x => x.Id == id).ToArray();

            if (!matchedTimers.Any())
            {
                if (timer == null)
                {
                    return Request.CreateErrorResponse(HttpStatusCode.BadRequest, "Missing timer body");
                }

                Timers.Add(timer);

                GetHubContext().Clients.All.timer(timer);

                return Request.CreateResponse(HttpStatusCode.Created);
            }

            foreach (var t in matchedTimers)
            {
                t.ApplyFrom(timer);
            }

            GetHubContext().Clients.All.timer(timer);

            return Request.CreateResponse(HttpStatusCode.Accepted);
        }

        public void Put([FromBody]RaceTimer timer)
        {
            if (timer == null)
            {
                return;
            }

            if (timer.Id == null || timer.Id == Guid.Empty)
            {
                timer.Id = Guid.NewGuid();
            }
            else
            {
                Delete(timer.Id);
            }

            Timers.Add(timer);

            GetHubContext().Clients.All.timer(timer);
        }

        public void Delete(Guid id)
        {
            foreach (var timer in Timers.Where(x => x.Id == id).ToArray())
            {
                Timers.Remove(timer);
            }

            GetHubContext().Clients.All.deleteTimer(id);
        }

        private IHubContext GetHubContext()
        {
            return GlobalHost.ConnectionManager.GetHubContext<TimerHub>();
        }
    }
}
