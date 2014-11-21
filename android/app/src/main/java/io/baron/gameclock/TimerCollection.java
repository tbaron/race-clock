package io.baron.gameclock;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.*;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;

public class TimerCollection {
	public static final String STATE = "timerState";

	public static TimerCollection instance;

	private final Context context;
	private final Gson gson;

	private long serverTimeOffset;

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

	public void getTimeOffset() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("https://race-clock.azurewebsites.net/api/server").openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
					connection.setRequestProperty("Accept", "application/json");
					connection.setUseCaches(false);

					String json = IOUtils.toString(connection.getInputStream());

					ServerModel model = gson.fromJson(json, ServerModel.class);

					if (model != null) {
						serverTimeOffset = model.time.getTime() - new Date().getTime();
					}

					int responseCode = connection.getResponseCode();

					Log.d("TBARON", "SERVER time diff (" + responseCode + "): " + serverTimeOffset);

					connection.disconnect();

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		}.execute();
	}

	public void postUpdate(GameTimer timer) {

		new AsyncTask<GameTimer, Void, Void>() {

			@Override
			protected Void doInBackground(GameTimer... params) {

				GameTimer localTimer = params[0];
				GameTimer timer = new GameTimer();
				timer.id = localTimer.id;
				timer.name = localTimer.name;
				timer.title = localTimer.title;
				timer.start = localTimer.start;
				timer.stop = localTimer.stop;

				if (timer.start != null) {
					Calendar skewedTime = Calendar.getInstance();
					skewedTime.setTimeInMillis(timer.start.getTime() + serverTimeOffset);

					timer.start = skewedTime.getTime();
				}
				
				if (timer.stop != null) {
					Calendar skewedTime = Calendar.getInstance();
					skewedTime.setTimeInMillis(timer.stop.getTime() + serverTimeOffset);

					timer.stop = skewedTime.getTime();
				}

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("https://race-clock.azurewebsites.net/api/timer/" + timer.id).openConnection();
					connection.setDoOutput(false);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setUseCaches(false);

					String json = gson.toJson(timer);
					DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

					outputStream.writeBytes(json);

					outputStream.flush();
					outputStream.close();

					int responseCode = connection.getResponseCode();

					Log.d("TBARON", "Updated (" + responseCode + "): " + json);

					connection.disconnect();

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
					connection.setDoOutput(false);
					connection.setDoInput(true);
					connection.setRequestMethod("DELETE");
					connection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
					connection.setUseCaches(false);

					int responseCode = connection.getResponseCode();

					Log.d("TBARON", "DELETED (" + responseCode + "): " + timer.id);

					connection.disconnect();

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
