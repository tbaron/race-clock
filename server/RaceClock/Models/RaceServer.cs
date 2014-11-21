using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;

namespace RaceClock.Models
{
    public class RaceServer
    {
        [JsonProperty("time")]
        public DateTime Time { get; set; }
    }
}