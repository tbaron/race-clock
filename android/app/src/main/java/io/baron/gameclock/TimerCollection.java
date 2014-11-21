package io.baron.gameclock;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.Date;

public class TimerCollection {
	public static final String STATE = "timerState";

	public static TimerCollection instance;

	private final Context context;
	private final Gson gson;

	public final GameTimerList timers = new GameTimerList();

	public TimerCollection(Context context) {
		this.context = context;
		this.gson = new GsonBuilder()
			.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return new JsonPrimitive(src == null ? 0 : src.getTime());
				}
			})
			.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
				@Override
				public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
					long value = json.getAsJsonPrimitive().getAsLong();
					return new Date(value);
				}
			})
			.create();
	}

	public void postUpdate(GameTimer timer) {

		new AsyncTask<GameTimer, Void, Void>() {

			@Override
			protected Void doInBackground(GameTimer... params) {

				GameTimer timer = params[0];

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("https://race-clock.azurewebsites.net/api/timer/" + timer.id).openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setUseCaches(false);

					DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
					String json = gson.toJson(timer);

					outputStream.writeBytes(URLEncoder.encode(json, "UTF-8"));

					outputStream.flush();
					outputStream.close();


					int responseCode = connection.getResponseCode();

					Log.d("TBARON", "Updated (" + responseCode + "): " + json);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		}.execute(timer);
	}

	public void postDelete(GameTimer timer) {

		new AsyncTask<GameTimer, Void, Void>() {

			@Override
			protected Void doInBackground(GameTimer... params) {

				GameTimer timer = params[0];

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("https://race-clock.azurewebsites.net/api/timer/" + timer.id).openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("DELETE");
					connection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
					connection.setUseCaches(false);

					int responseCode = connection.getResponseCode();

					Log.d("TBARON", "DELETED (" + responseCode + "): " + timer.id);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		}.execute(timer);
	}

	public void save() {
		String json = gson.toJson(this.timers);

		context
			.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE)
			.edit()
			.putString(STATE, json)
			.commit();
	}

	public void restore() {
		String json = context
			.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE)
			.getString(STATE, "[]");

		GameTimerList timers = gson.fromJson(json, GameTimerList.class);

		this.timers.clear();
		this.timers.addAll(timers);
	}
}
