using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace RaceClock
{
    public class EpochDateTimeConverter : DateTimeConverterBase
    {
        internal readonly static DateTime Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            long milliseconds = serializer.Deserialize<long>(reader);

            return ConvertMillisecondsToDate(milliseconds);
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            long milliseconds = ConvertDateToMilliseconds((DateTime)value);

            writer.WriteValue(milliseconds);
        }

        private static DateTime ConvertMillisecondsToDate(long milliseconds)
        {
            if (milliseconds < 0)
            {
                throw new ArgumentException("Milliseconds must be greater than 0.");
            }

            return Epoch.AddMilliseconds(milliseconds);
        }

        private static long ConvertDateToMilliseconds(DateTime date)
        {
            date = date.ToUniversalTime();

            if (date < Epoch)
            {
                return 0;
            }

            return (long)(date - Epoch).TotalMilliseconds;
        }
    }
}