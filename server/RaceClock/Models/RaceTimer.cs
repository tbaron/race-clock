using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;

namespace RaceClock.Models
{
    public class RaceTimer
    {
        [JsonProperty("id")]
        public Guid Id { get; set; }

        [JsonProperty("title")]
        public string Title { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("start")]
        public DateTime Start { get; set; }

        [JsonProperty("stop")]
        public DateTime Stop { get; set; }

        public void ApplyFrom(RaceTimer timer)
        {
            this.Name = timer.Name;
            this.Start = timer.Start;
            this.Stop = timer.Stop;
            this.Title = timer.Title;
        }
    }
}
